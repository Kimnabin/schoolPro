package school.xxxx.application.mapper.user;

import org.springframework.stereotype.Component;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.domain.model.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserListMapper {

    private final UserMapper userMapper;

    public UserListMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<UserResponseDTO> toDTOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

}
