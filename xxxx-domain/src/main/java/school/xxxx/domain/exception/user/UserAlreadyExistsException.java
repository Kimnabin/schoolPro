package school.xxxx.domain.exception.user;

import school.xxxx.domain.exception.DomainException;

public class UserAlreadyExistsException extends DomainException {

    private static final String ERROR_CODE = "USER_ALREADY_EXISTS";

    public UserAlreadyExistsException(String message) {
        super(message, ERROR_CODE);
    }

    public UserAlreadyExistsException(String field, String value) {
        super("User already exists with " + field + ": " + value, ERROR_CODE);
    }
}