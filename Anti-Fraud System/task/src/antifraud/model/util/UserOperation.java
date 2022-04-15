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
public class UserOperation {

    String username;
    Operation operation;

}
