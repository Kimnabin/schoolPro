package school.xxxx.application.model.dto.user.request;

import jakarta.validation.constraints.Email;
// Annotation kiểm tra định dạng email hợp lệ
import jakarta.validation.constraints.NotBlank;
// Annotation kiểm tra chuỗi không được null hoặc rỗng (sau khi trim)
import jakarta.validation.constraints.Pattern;
// Annotation kiểm tra chuỗi theo regex pattern
import jakarta.validation.constraints.Size;
// Annotation kiểm tra độ dài chuỗi
import lombok.Data;
// Lombok tự động generate getter/setter, toString, equals, hashCode

@Data
// Lombok @Data: tạo getter, setter, equals, hashCode, toString tự động
public class UserCreateReqDTO {

    @NotBlank(message = "Username is required")
    // Không được null, không được chuỗi rỗng hoặc toàn khoảng trắng
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    // Yêu cầu username dài từ 3 đến 50 ký tự
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can only contain letters, numbers, and underscores"
    )
    // Username chỉ được chứa chữ cái (hoa/thường), số, và dấu gạch dưới (_)
    private String username;

    @NotBlank(message = "Password is required")
    // Password bắt buộc không được để trống
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    // Độ dài mật khẩu tối thiểu 8 ký tự, tối đa 100 ký tự
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    // Regex yêu cầu mật khẩu có ít nhất:
    // - 1 chữ hoa
    // - 1 chữ thường
    // - 1 chữ số
    // - 1 ký tự đặc biệt (@$!%*?&)
    private String password;

    @NotBlank(message = "Full name is required")
    // Họ tên không được bỏ trống
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    // Tên tối thiểu 2 ký tự, tối đa 100 ký tự
    private String fullName;

    @NotBlank(message = "Email is required")
    // Email bắt buộc không được để trống
    @Email(message = "Email must be valid")
    // Kiểm tra định dạng email hợp lệ
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    // Email tối đa 100 ký tự
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be between 10 and 15 digits"
    )
    // Số điện thoại chỉ chứa số, có thể bắt đầu bằng dấu +, độ dài 10-15 chữ số
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    // Địa chỉ tối đa 255 ký tự, không bắt buộc nhập
    private String address;

    private Boolean state;
    // Trạng thái tài khoản (true = active, false = inactive), không có ràng buộc validation
}
