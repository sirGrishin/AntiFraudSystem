package antifraud.service;

import antifraud.exeption.StolenCardExistException;
import antifraud.model.Ip;
import antifraud.model.StolenCard;
import antifraud.model.util.Status;
import antifraud.repo.StolenCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ilya Grishin
 */
@Service
@RequiredArgsConstructor
public class StolenCardService {

    private final StolenCardRepository stolenCardRepository;


    public void saveStolenCard(StolenCard stolenCard) {
        if (stolenCardNotExist(stolenCard.getNumber())) {
            stolenCardRepository.save(stolenCard);
        } else throw new StolenCardExistException();

    }

    public Status deleteByNumber(String number) {
        StolenCard byNumber = stolenCardRepository.findByNumber(number);
        if (byNumber == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Status status = new Status();
        stolenCardRepository.delete(byNumber);
        status.setStatus("Card " + number + " successfully removed!");
        return status;
    }

    public List<StolenCard> getAll() {
        List<StolenCard> allByOrderByIdAsc = stolenCardRepository.findAllByOrderByIdAsc();
        if(allByOrderByIdAsc.isEmpty()){
            return new ArrayList<>();
        }
        else return allByOrderByIdAsc;
    }

    /**
     * true если карты не существует
     */
    private boolean stolenCardNotExist(String number) {
        return stolenCardRepository.findByNumber(number) == null;
    }


}
