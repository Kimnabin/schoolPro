package school.xxxx.application.model.dto.user.request;

import lombok.Data;

@Data
public class UserCreateReqDTO {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Integer phoneNumber;
    private String address;
}