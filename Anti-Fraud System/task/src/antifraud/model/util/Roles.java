package antifraud.model.util;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Ilya Grishin
 */
public enum Roles implements GrantedAuthority {
    ADMINISTRATOR,
    MERCHANT,
    SUPPORT;

    @Override
    public String getAuthority() {
        return name();
    }
}
