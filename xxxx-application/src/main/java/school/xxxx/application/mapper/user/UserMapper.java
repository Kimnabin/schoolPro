package school.xxxx.application.mapper.user;

import org.springframework.stereotype.Component;
import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.domain.model.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserCreateReqDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setState(dto.getState());
        user.setPhoneNumber(Integer.valueOf(dto.getPhoneNumber()));
        user.setAddress(dto.getAddress());

        return user;
    }

    /**
     * Update existing User entity from DTO fields.
     * Only non-null fields in DTO will be updated.
     */
    public void updateEntity(UserUpdateReqDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

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

    public UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
//        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());

        return dto;
    }
}
