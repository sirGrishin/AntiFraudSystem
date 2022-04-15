package antifraud.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Ilya Grishin
 */
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Stolen card exist!")
public class StolenCardExistException extends RuntimeException {
}