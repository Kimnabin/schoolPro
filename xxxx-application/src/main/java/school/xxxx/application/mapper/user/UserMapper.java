package school.xxxx.application.mapper.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.application.util.PhoneNumberUtil;
import school.xxxx.domain.model.entity.User;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    /**
     * Chuyển đổi DTO tạo mới thành Entity User
     */
    public User toEntity(UserCreateReqDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();

        if (StringUtils.hasText(dto.getUsername())) {
            user.setUsername(dto.getUsername().trim());
        }

        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (StringUtils.hasText(dto.getFullName())) {
            user.setFullName(dto.getFullName().trim());
        }

        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail().trim().toLowerCase());
        }

        user.setState(dto.getState());


        if (StringUtils.hasText(dto.getPhoneNumber())) {
            String normalizedPhone = PhoneNumberUtil.normalizePhoneNumber(dto.getPhoneNumber());
            user.setPhoneNumber(normalizedPhone);
        }

        if (StringUtils.hasText(dto.getAddress())) {
            user.setAddress(dto.getAddress().trim());
        }

        return user;
    }

    /**
     * Cập nhật User entity từ DTO update
     */
    public void updateEntity(UserUpdateReqDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        if (StringUtils.hasText(dto.getFullName())) {
            user.setFullName(dto.getFullName().trim());
        }

        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail().trim().toLowerCase());
        }


        if (StringUtils.hasText(dto.getPhoneNumber())) {
            String normalizedPhone = PhoneNumberUtil.normalizePhoneNumber(dto.getPhoneNumber());
            user.setPhoneNumber(normalizedPhone);
        }

        if (StringUtils.hasText(dto.getAddress())) {
            user.setAddress(dto.getAddress().trim());
        }
    }

    /**
     * Chuyển đổi User entity thành DTO response
     */
    public UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());

        if (StringUtils.hasText(user.getPhoneNumber())) {
            dto.setPhoneNumber(PhoneNumberUtil.formatForDisplay(user.getPhoneNumber()));
        }

        dto.setAddress(user.getAddress());

        return dto;
    }
}