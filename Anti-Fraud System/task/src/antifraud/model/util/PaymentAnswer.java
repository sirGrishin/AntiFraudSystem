package antifraud.model.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Ilya Grishin
 */
@Getter
@Setter
@RequiredArgsConstructor
public class PaymentAnswer {
    Result result;
    String info;
}
