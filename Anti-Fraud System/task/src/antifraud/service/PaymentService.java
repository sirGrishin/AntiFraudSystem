package antifraud.service;

import antifraud.exeption.PaymentFeedbackConflict;
import antifraud.model.Ip;
import antifraud.model.Payment;
import antifraud.model.StolenCard;
import antifraud.model.util.PaymentAnswer;
import antifraud.model.util.PaymentFeedback;
import antifraud.model.util.Result;
import antifraud.repo.IpRepository;
import antifraud.repo.PaymentRepository;
import antifraud.repo.StolenCardRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ilya Grishin
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private static String NONE = "none";
    private static String AMOUNT = "amount";
    private static String IP = "ip";
    private static String NUMBER = "card-number";
    private static String QUOTE = ", ";
    private static String IPCORRELATION = "ip-correlation";
    private static String REGIONCORRELATION = "region-correlation";
    private static Long allowedLimit = 200L;
    private static Long manualLimit = 1500L;
    private final StolenCardRepository stolenCardRepository;
    private final IpRepository ipRepository;
    private final PaymentRepository paymentRepository;

    public PaymentAnswer getResult(Payment payment) {
        save(payment);
        PaymentAnswer paymentAnswer = new PaymentAnswer();
        StolenCard byNumber = stolenCardRepository.findByNumber(payment.getNumber());
        Ip byIp = ipRepository.findByIp(payment.getIp());
        StringBuilder stringBuilder = new StringBuilder();

        TransactionCount transactionCount = transactionCount(payment);

        Long amount = payment.getAmount();


        if (byIp != null | byNumber != null | amount > manualLimit | transactionCount.numberUniqueIp > 3 | transactionCount.numberUniqueRegions > 3) {
            paymentAnswer.setResult(Result.PROHIBITED);
        }
        if (transactionCount.getNumberUniqueIp().equals(3L) | transactionCount.numberUniqueRegions == 3) {
            paymentAnswer.setResult(Result.MANUAL_PROCESSING);
        }

        if (amount <= allowedLimit & paymentAnswer.getResult() == null) {
            paymentAnswer.setResult(Result.ALLOWED);
            paymentAnswer.setInfo(String.valueOf(stringBuilder.append(NONE)));
            payment.setResult(paymentAnswer.getResult());
            save(payment);
            return paymentAnswer;
        }
        if (amount > allowedLimit & amount <= manualLimit & paymentAnswer.getResult() != Result.PROHIBITED) {
            paymentAnswer.setResult(Result.MANUAL_PROCESSING);
            paymentAnswer.setInfo(String.valueOf(stringBuilder.append(AMOUNT).append(QUOTE)));
        }

        if (paymentAnswer.getResult().equals(Result.PROHIBITED)) {
            if (amount > manualLimit) {
                stringBuilder.append(AMOUNT).append(QUOTE);
            }
            if (byNumber != null) {
                stringBuilder.append(NUMBER).append(QUOTE);
            }
            if (byIp != null) {
                stringBuilder.append(IP).append(QUOTE);
            }
            if (transactionCount.numberUniqueIp > 3) {
                stringBuilder.append(IPCORRELATION).append(QUOTE);
            }
            if (transactionCount.numberUniqueRegions > 3) {
                stringBuilder.append(REGIONCORRELATION).append(QUOTE);
            }
        }

        if (paymentAnswer.getResult().equals(Result.MANUAL_PROCESSING)) {
            if (transactionCount.numberUniqueIp == 3) {
                stringBuilder.append(IPCORRELATION).append(QUOTE);
            }
            if (transactionCount.numberUniqueRegions == 3) {
                stringBuilder.append(REGIONCORRELATION).append(QUOTE);
            }
        }

        stringBuilder.setLength(stringBuilder.length() - 2);
        paymentAnswer.setInfo(stringBuilder.toString());
        payment.setResult(paymentAnswer.getResult());
        save(payment);
        return paymentAnswer;
    }

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    public List<Payment> getAllByNumber(String number) {
        return paymentRepository.findAllByNumber(number).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment updatePayment(PaymentFeedback paymentFeedback) {
        Payment byId = paymentRepository.findById(paymentFeedback.getTransactionId()).orElse(null);
        if(byId==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Long amount = byId.getAmount();
        Result feedback = paymentFeedback.getFeedback();

        if (!(byId.getFeedback() =="")) {
            throw new PaymentFeedbackConflict();
        }
            if (byId.getResult().equals(Result.ALLOWED)) {
                switch (feedback) {
                    case ALLOWED:
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                    case MANUAL_PROCESSING:
                        decreasingAllowedLimit(amount);
                        return savePaymentNewFeedback(byId, paymentFeedback);
                    case PROHIBITED:
                        decreasingAllowedLimit(amount);
                        decreasingManualLimit(amount);
                        return savePaymentNewFeedback(byId, paymentFeedback);
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            } else if (byId.getResult().equals(Result.MANUAL_PROCESSING)) {
                switch (feedback) {
                    case ALLOWED:
                        increasingAllowedLimit(amount);
                        return savePaymentNewFeedback(byId, paymentFeedback);
                    case MANUAL_PROCESSING:
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                    case PROHIBITED:
                        decreasingManualLimit(amount);
                        return savePaymentNewFeedback(byId, paymentFeedback);
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            } else if (byId.getResult().equals(Result.PROHIBITED)) {
                switch (feedback) {
                    case ALLOWED:
                        increasingAllowedLimit(amount);
                        increasingManualLimit(amount);
                        return savePaymentNewFeedback(byId, paymentFeedback);
                    case MANUAL_PROCESSING:
                        increasingManualLimit(amount);
                        return savePaymentNewFeedback(byId, paymentFeedback);
                    case PROHIBITED:
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    private void increasingAllowedLimit(Long amount) {
        allowedLimit = (long) Math.ceil(0.8 * allowedLimit + 0.2 * amount);
    }

    private void decreasingAllowedLimit(Long amount) {
        allowedLimit = (long) Math.ceil(0.8 * allowedLimit - 0.2 * amount);
    }

    private void increasingManualLimit(Long amount) {
        manualLimit = (long) Math.ceil(0.8 * manualLimit + 0.2 * amount);
    }

    private void decreasingManualLimit(Long amount) {
        manualLimit = (long) Math.ceil(0.8 * manualLimit - 0.2 * amount);
    }

    private Payment savePaymentNewFeedback(Payment payment, PaymentFeedback paymentFeedback) {
        payment.setFeedback(paymentFeedback.getFeedback().toString());
        save(payment);
        return payment;
    }

    private TransactionCount transactionCount(Payment payment) {
        String number = payment.getNumber();
        LocalDateTime date = payment.getDate();
        List<Payment> allByNumberAndDateBetween = paymentRepository.findAllByNumberAndDateBetween(number, date.minusHours(1), date);

        long ip = allByNumberAndDateBetween.stream().map(Payment::getIp).collect(Collectors.toList()).stream().distinct().count();

        long region = allByNumberAndDateBetween.stream().map(Payment::getRegion).collect(Collectors.toList()).stream().distinct().count();

        return TransactionCount.builder()
                .numberPayments(allByNumberAndDateBetween.size())
                .numberUniqueIp(ip)
                .numberUniqueRegions(region).build();
    }

    @Getter
    @Setter
    @Builder
    private static class TransactionCount {
        private Integer numberPayments;
        private Long numberUniqueIp;
        private Long numberUniqueRegions;
    }
}
