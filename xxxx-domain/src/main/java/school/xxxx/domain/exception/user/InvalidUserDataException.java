package school.xxxx.domain.exception.user;

import school.xxxx.domain.exception.DomainException;

public class InvalidUserDataException extends DomainException {

    private static final String ERROR_CODE = "INVALID_USER_DATA";

    public InvalidUserDataException(String message) {
        super(message, ERROR_CODE);
    }

    public InvalidUserDataException(String field, String reason) {
        super("Invalid " + field + ": " + reason, ERROR_CODE);
    }
}
