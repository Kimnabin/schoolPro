package school.xxxx.controller.exception;

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
import school.xxxx.controller.model.enums.ResultCode;
import school.xxxx.controller.model.enums.ResultUtil;
import school.xxxx.controller.model.vo.ResultMessage;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi validation cho @Valid
     */
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

    /**
     * Xử lý lỗi validation cho @Validated
     */
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

    /**
     * Xử lý lỗi bind
     */
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

    /**
     * Xử lý lỗi type mismatch
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResultMessage<String>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch error: {}", ex.getMessage());

        String errorMessage = String.format("Invalid parameter '%s': expected type %s",
                ex.getName(), ex.getRequiredType().getSimpleName());

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), errorMessage));
    }

    /**
     * Xử lý IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultMessage<String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), ex.getMessage()));
    }

    /**
     * Xử lý RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultMessage<String>> handleRuntimeException(
            RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultUtil.error(ResultCode.ERROR.code(),
                        "An unexpected error occurred"));
    }

    /**
     * Xử lý Exception chung
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultMessage<String>> handleGenericException(
            Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultUtil.error(ResultCode.ERROR.code(),
                        "An unexpected error occurred"));
    }
}