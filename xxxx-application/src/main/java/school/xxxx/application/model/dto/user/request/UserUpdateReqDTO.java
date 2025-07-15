package school.xxxx.application.model.dto.user.request;

import lombok.Data;

@Data
public class UserUpdateReqDTO {

    private Long id;
    private String fullName;
    private String email;
    private Integer phoneNumber;
    private String address;
}
