package school.xxxx.application.model.dto.user.response;

import lombok.Data;
// Lombok @Data: tự động sinh getter, setter, equals, hashCode, toString

@Data
public class UserResponseDTO {

    private Long id;
    // ID duy nhất của user, dùng để định danh

//    private String username;
//    Username có thể bỏ qua trong response (ví dụ vì lý do bảo mật hoặc không cần thiết hiển thị)

    private String fullName;
    // Họ tên đầy đủ của user

    private String email;
    // Email liên hệ của user

    private String phoneNumber;
    // Số điện thoại của user, có thể null nếu không cung cấp

    private String address;
    // Địa chỉ của user
}
