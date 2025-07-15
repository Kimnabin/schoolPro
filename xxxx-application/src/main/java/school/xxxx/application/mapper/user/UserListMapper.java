package school.xxxx.application.mapper.user;

import org.springframework.stereotype.Component;
// @Component: Đánh dấu class này là một Spring Bean, để Spring có thể quản lý và inject
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
// DTO đại diện dữ liệu trả về cho client
import school.xxxx.domain.model.entity.User;
// Entity User đại diện cho bảng user trong database

import java.util.Collections;
// Collections.emptyList() trả về một List rỗng, tránh null
import java.util.List;
import java.util.stream.Collectors;
// Collectors dùng để thu thập kết quả từ Stream thành List

@Component
// Spring sẽ tự động tạo Bean UserListMapper, có thể inject vào service/controller
public class UserListMapper {

    private final UserMapper userMapper;
    // UserMapper là một mapper khác, chịu trách nhiệm convert User -> UserResponseDTO

    // Constructor injection: Spring tự động inject UserMapper khi tạo Bean này
    public UserListMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Chuyển đổi danh sách User (entity) sang danh sách UserResponseDTO (DTO)
     *
     * @param users: danh sách User lấy từ DB
     * @return danh sách UserResponseDTO đã convert,
     *         hoặc list rỗng nếu input null hoặc empty
     */
    public List<UserResponseDTO> toDTOList(List<User> users) {
        // Nếu danh sách null hoặc rỗng thì trả về Collections.emptyList() (List immutable rỗng)
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        // Nếu có dữ liệu, dùng Stream để map từng User -> UserResponseDTO
        return users.stream()
                .map(userMapper::toDTO) // gọi UserMapper.toDTO(user)
                .collect(Collectors.toList()); // thu thập thành List<UserResponseDTO>
    }

}
