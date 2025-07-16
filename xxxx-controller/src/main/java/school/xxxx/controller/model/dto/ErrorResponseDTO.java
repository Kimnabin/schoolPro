package school.xxxx.controller.model.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponseDTO {

    private String errorCode;
    private String message;
    private int status;
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private Map<String, String> validationErrors;
    private String requestId; // For tracking
}