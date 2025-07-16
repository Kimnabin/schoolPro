package school.xxxx.application.mapper.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.domain.model.entity.User;

@Component
@RequiredArgsConstructor // Lombok tự sinh constructor cho final fields
public class UserMapper {

    // Inject PasswordEncoder thông qua constructor (recommended)
    private final PasswordEncoder passwordEncoder;

    /**
     * Chuyển đổi DTO tạo mới (UserCreateReqDTO) thành Entity User.
     * Dùng khi tạo User mới từ request.
     */
    public User toEntity(UserCreateReqDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();

        // Validate và set username
        if (StringUtils.hasText(dto.getUsername())) {
            user.setUsername(dto.getUsername().trim());
        }

        // Hash password trước khi lưu
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Validate và set fullName
        if (StringUtils.hasText(dto.getFullName())) {
            user.setFullName(dto.getFullName().trim());
        }

        // Validate và set email (lowercase + trim)
        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail().trim().toLowerCase());
        }

        // Set state
        user.setState(dto.getState());

        // Validate và convert phoneNumber
        if (StringUtils.hasText(dto.getPhoneNumber())) {
            try {
                user.setPhoneNumber(Integer.valueOf(dto.getPhoneNumber().trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid phone number format: " + dto.getPhoneNumber());
            }
        }

        // Set address
        if (StringUtils.hasText(dto.getAddress())) {
            user.setAddress(dto.getAddress().trim());
        }

        return user;
    }

    /**
     * Cập nhật User entity hiện có từ DTO update.
     * Chỉ cập nhật các field không null trong DTO (partial update).
     */
    public void updateEntity(UserUpdateReqDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        // Chỉ update nếu field không null → tránh ghi đè bằng giá trị null
        if (StringUtils.hasText(dto.getFullName())) {
            user.setFullName(dto.getFullName().trim());
        }

        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail().trim().toLowerCase());
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (StringUtils.hasText(dto.getAddress())) {
            user.setAddress(dto.getAddress().trim());
        }
    }

    /**
     * Chuyển đổi User entity thành DTO trả về client.
     */
    public UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());

        return dto;
    }
}