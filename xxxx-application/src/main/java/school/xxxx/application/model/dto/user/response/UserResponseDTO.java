package school.xxxx.application.model.dto.user.response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Integer phoneNumber;
    private String address;
}
