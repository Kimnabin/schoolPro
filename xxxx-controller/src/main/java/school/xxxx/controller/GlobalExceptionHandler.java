package school.xxxx.controller;

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

@RestControllerAdvice // Đánh dấu đây là class xử lý ngoại lệ toàn cục cho REST Controller
@Slf4j              // Tạo sẵn logger để log lỗi hoặc thông tin
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi validation với @Valid cho request body (DTO)
     * Ví dụ: @Valid UserCreateReqDTO
     * Bắt lỗi MethodArgumentNotValidException khi các trường DTO không hợp lệ
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultMessage<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        // Lấy từng lỗi field, đưa vào map <field, message>
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();  // Tên trường bị lỗi
            String errorMessage = error.getDefaultMessage();     // Thông báo lỗi tương ứng
            errors.put(fieldName, errorMessage);
        });

        // Trả về HTTP 400 Bad Request với message lỗi chuẩn theo định dạng ResultMessage
        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                        "Validation failed: " + errors.toString()));
    }

    /**
     * Xử lý lỗi validation với @Validated cho tham số method (query, path)
     * Bắt lỗi ConstraintViolationException khi tham số không hợp lệ
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResultMessage<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        // Lấy từng lỗi trong danh sách violations
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString(); // Tên tham số
            String errorMessage = violation.getMessage();               // Thông báo lỗi
            errors.put(fieldName, errorMessage);
        }

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(),
                        "Validation failed: " + errors.toString()));
    }

    /**
     * Xử lý lỗi binding dữ liệu đầu vào (ví dụ truyền sai kiểu dữ liệu hoặc định dạng)
     * Bắt lỗi BindException
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
     * Xử lý lỗi khi kiểu tham số truyền vào không đúng với kiểu mong đợi
     * Ví dụ: truyền chuỗi cho một tham số Long
     * Bắt lỗi MethodArgumentTypeMismatchException
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
     * Xử lý lỗi IllegalArgumentException thường dùng để validate thủ công hoặc logic sai tham số
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultMessage<String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), ex.getMessage()));
    }

    /**
     * Bắt chung tất cả RuntimeException không được xử lý khác
     * Trả về lỗi 500 Internal Server Error
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
     * Bắt chung tất cả Exception không được xử lý khác
     * Trả về lỗi 500 Internal Server Error
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
