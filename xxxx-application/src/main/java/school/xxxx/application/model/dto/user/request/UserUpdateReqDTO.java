package school.xxxx.application.model.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
// Lombok @Data: tự động sinh getter, setter, equals, hashCode, toString

@Data
public class UserUpdateReqDTO {

    private Long id;
    // ID của User cần update (thường không bắt buộc trong DTO update nếu ID được truyền qua URL,
    // nhưng vẫn có thể dùng khi update trực tiếp bằng payload)

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    // Họ tên mới (optional), nếu null thì không update → hỗ trợ partial update

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;
    // Email mới (optional), nếu null thì giữ nguyên

    @Pattern(
            regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10,14}$",
            message = "Phone number must be valid format (10-14 digits, optional country code)"
    )
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;
    // Địa chỉ mới (optional)
}
