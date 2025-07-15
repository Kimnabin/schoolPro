package school.xxxx.application.model.dto.user.request;

import lombok.Data;
// Lombok @Data: tự động sinh getter, setter, equals, hashCode, toString

@Data
public class UserUpdateReqDTO {

    private Long id;
    // ID của User cần update (thường không bắt buộc trong DTO update nếu ID được truyền qua URL,
    // nhưng vẫn có thể dùng khi update trực tiếp bằng payload)

    private String fullName;
    // Họ tên mới (optional), nếu null thì không update → hỗ trợ partial update

    private String email;
    // Email mới (optional), nếu null thì giữ nguyên

    private Integer phoneNumber;
    // Số điện thoại mới (optional). Ở đây dùng Integer → không lưu được số có dấu "+" hoặc số bắt đầu bằng 0 →
    // có thể cân nhắc dùng String thay vì Integer

    private String address;
    // Địa chỉ mới (optional)
}
