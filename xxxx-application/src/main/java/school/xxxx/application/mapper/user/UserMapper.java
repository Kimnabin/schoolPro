package school.xxxx.application.mapper.user;

import org.springframework.stereotype.Component;
// @Component: Đánh dấu đây là một Spring Bean để Spring tự động quản lý

import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
// DTO chứa dữ liệu từ client khi tạo mới User
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
// DTO chứa dữ liệu từ client khi update User
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
// DTO trả về cho client sau khi xử lý
import school.xxxx.domain.model.entity.User;
// Entity User đại diện cho bảng user trong database

@Component
// Spring sẽ tự động quét class này và tạo Bean để có thể inject vào nơi khác
public class UserMapper {

    /**
     * Chuyển đổi DTO tạo mới (UserCreateReqDTO) thành Entity User.
     * Dùng khi tạo User mới từ request.
     *
     * @param dto: dữ liệu yêu cầu tạo user
     * @return User entity tương ứng hoặc null nếu dto null
     */
    public User toEntity(UserCreateReqDTO dto) {
        if (dto == null) {
            return null; // tránh NullPointerException
        }

        User user = new User(); // tạo entity mới
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setState(dto.getState());
        // convert phoneNumber từ String sang Integer (cần đảm bảo dữ liệu hợp lệ)
        user.setPhoneNumber(Integer.valueOf(dto.getPhoneNumber()));
        user.setAddress(dto.getAddress());

        return user; // trả về entity đã được map dữ liệu
    }

    /**
     * Cập nhật User entity hiện có từ DTO update.
     * Chỉ cập nhật các field không null trong DTO (partial update).
     *
     * @param dto: DTO chứa dữ liệu cần update
     * @param user: entity user cần được cập nhật
     */
    public void updateEntity(UserUpdateReqDTO dto, User user) {
        if (dto == null || user == null) {
            return; // không làm gì nếu một trong hai null
        }

        // Chỉ update nếu field không null → tránh ghi đè bằng giá trị null
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
    }

    /**
     * Chuyển đổi User entity thành DTO trả về client.
     *
     * @param user: entity lấy từ DB
     * @return DTO để trả về client hoặc null nếu user null
     */
    public UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null; // tránh NullPointerException
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        // dto.setUsername(user.getUsername()); // tạm thời comment, có thể ẩn username vì lý do bảo mật
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());

        return dto; // trả về DTO đã map dữ liệu
    }
}
