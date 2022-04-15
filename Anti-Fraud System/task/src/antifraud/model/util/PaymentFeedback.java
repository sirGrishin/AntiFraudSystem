package antifraud.model.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Ilya Grishin
 */
@RequiredArgsConstructor
public class PaymentFeedback {
    Long transactionId;
    Result feedback;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Result getFeedback() {
        return feedback;
    }

    public void setFeedback(Result feedback) {
        this.feedback = feedback;
    }
}
