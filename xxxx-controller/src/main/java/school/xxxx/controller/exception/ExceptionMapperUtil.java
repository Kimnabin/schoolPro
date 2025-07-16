package school.xxxx.controller.exception;

import org.springframework.http.HttpStatus;
import school.xxxx.domain.exception.DomainException;
import school.xxxx.domain.exception.user.UserNotFoundException;
import school.xxxx.domain.exception.user.UserAlreadyExistsException;
import school.xxxx.domain.exception.user.InvalidUserDataException;
import school.xxxx.domain.exception.BusinessRuleViolationException;

/**
 * Utility class để map Domain Exceptions sang HTTP Status Codes
 * Chỉ Controller layer biết về HTTP, Domain layer không cần biết
 */
public class ExceptionMapperUtil {

    public static HttpStatus mapToHttpStatus(DomainException domainException) {
        return switch (domainException) {
            case UserNotFoundException ignored -> HttpStatus.NOT_FOUND;
            case UserAlreadyExistsException ignored -> HttpStatus.CONFLICT;
            case InvalidUserDataException ignored -> HttpStatus.BAD_REQUEST;
            case BusinessRuleViolationException ignored -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    public static String mapToResultCode(DomainException domainException) {
        return switch (domainException) {
            case UserNotFoundException ignored -> "USER_NOT_FOUND";
            case UserAlreadyExistsException ignored -> "USER_ALREADY_EXISTS";
            case InvalidUserDataException ignored -> "PARAMS_ERROR";
            case BusinessRuleViolationException ignored -> "BUSINESS_RULE_VIOLATION";
            default -> "ERROR";
        };
    }
}