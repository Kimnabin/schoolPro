package school.xxxx.domain.exception.user;

import school.xxxx.domain.exception.DomainException;

public class UserNotFoundException extends DomainException {

    private static final String ERROR_CODE = "USER_NOT_FOUND";

    public UserNotFoundException(String message) {
        super(message, ERROR_CODE);
    }

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId, ERROR_CODE);
    }

    public UserNotFoundException(String field, String value) {
        super("User not found with " + field + ": " + value, ERROR_CODE);
    }
}