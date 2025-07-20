package school.xxxx.application.model.dto.auth.response;

import lombok.Builder;
import lombok.Data;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    private UserResponseDTO user;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    // Additional metadata
    private String sessionId;
    private String userAgent;
    private String ipAddress;
}
