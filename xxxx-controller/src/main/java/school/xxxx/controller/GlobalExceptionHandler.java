package school.xxxx.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import school.xxxx.domain.exception.DomainException;
import school.xxxx.domain.exception.user.UserNotFoundException;
import school.xxxx.domain.exception.user.UserAlreadyExistsException;
import school.xxxx.domain.exception.user.InvalidUserDataException;
import school.xxxx.controller.exception.ExceptionMapperUtil;
import school.xxxx.controller.model.enums.ResultCode;
import school.xxxx.controller.model.enums.ResultUtil;
import school.xxxx.controller.model.vo.ResultMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // =================================================================
    // DOMAIN EXCEPTIONS MAPPING (Clean Architecture approach)
    // =================================================================

    /**
     * Xử lý tất cả Domain Exceptions - Generic handler
     * Domain exceptions không biết gì về HTTP, Controller layer sẽ map chúng
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ResultMessage<String>> handleDomainException(
            DomainException ex, HttpServletRequest request) {

        String requestId = UUID.randomUUID().toString();
        HttpStatus httpStatus = ExceptionMapperUtil.mapToHttpStatus(ex);
        String resultCode = ExceptionMapperUtil.mapToResultCode(ex);

        log.error("Domain Exception [RequestId: {}] [Type: {}]: {}",
                requestId, ex.getClass().getSimpleName(), ex.getMessage());

        // Map domain exception sang HTTP response
        Integer code = switch (ex.getErrorCode()) {
            case "USER_NOT_FOUND" -> ResultCode.USER_NOT_FOUND.code();
            case "USER_ALREADY_EXISTS" -> ResultCode.PARAMS_ERROR.code();
            case "INVALID_USER_DATA" -> ResultCode.PARAMS_ERROR.code();
            case "BUSINESS_RULE_VIOLATION" -> ResultCode.PARAMS_ERROR.code();
            default -> ResultCode.ERROR.code();
        };

        return ResponseEntity.status(httpStatus)
                .body(ResultUtil.error(code, ex.getMessage()));
    }

    // =================================================================
    // SPECIFIC DOMAIN EXCEPTION HANDLERS (Optional - for specific handling)
    // =================================================================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResultMessage<String>> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {

        String requestId = UUID.randomUUID().toString();
        log.error("UserNotFoundException [RequestId: {}]: {}", requestId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResultMessage<String>> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, HttpServletRequest request) {

        String requestId = UUID.randomUUID().toString();
        log.error("UserAlreadyExistsException [RequestId: {}]: {}", requestId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ResultMessage<String>> handleInvalidUserDataException(
            InvalidUserDataException ex, HttpServletRequest request) {

        String requestId = UUID.randomUUID().toString();
        log.error("InvalidUserDataException [RequestId: {}]: {}", requestId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), ex.getMessage()));
    }

    // =================================================================
    // FRAMEWORK VALIDATION EXCEPTIONS (Existing code)
    // =================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultMessage<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                        "Validation failed: " + errors.toString()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResultMessage<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                        "Validation failed: " + errors.toString()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResultMessage<Map<String, String>>> handleBindException(
            BindException ex) {
        log.error("Bind error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                        "Bind failed: " + errors.toString()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResultMessage<String>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch error: {}", ex.getMessage());

        String errorMessage = String.format("Invalid parameter '%s': expected type %s",
                ex.getName(), ex.getRequiredType().getSimpleName());

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), errorMessage));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultMessage<String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultMessage<String>> handleRuntimeException(
            RuntimeException ex) {
        String requestId = UUID.randomUUID().toString();
        log.error("Runtime error [RequestId: {}]: {}", requestId, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultUtil.error(ResultCode.ERROR.code(),
                        "An unexpected error occurred. RequestId: " + requestId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultMessage<String>> handleGenericException(
            Exception ex) {
        String requestId = UUID.randomUUID().toString();
        log.error("Unexpected error [RequestId: {}]: {}", requestId, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultUtil.error(ResultCode.ERROR.code(),
                        "An unexpected error occurred. RequestId: " + requestId));
    }
}