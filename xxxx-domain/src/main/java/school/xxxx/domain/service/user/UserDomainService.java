package school.xxxx.domain.service.user;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import school.xxxx.domain.model.entity.User;

import java.util.List;

/**
 * Interface định nghĩa các nghiệp vụ liên quan đến User trong tầng Domain Service.
 * Đây là nơi tập trung xử lý logic nghiệp vụ trước khi gọi Repository thao tác dữ liệu.
 */
public interface UserDomainService {

    /**
     * Lấy User theo ID.
     * @param userId ID của người dùng cần lấy
     * @return User nếu tìm thấy, nếu không có có thể ném ngoại lệ hoặc trả null (tùy implement)
     */
    User getUserById(Long userId);

    /**
     * Lấy User theo username.
     * @param username tên đăng nhập của người dùng
     * @return User nếu tồn tại, null hoặc exception nếu không tìm thấy
     */
    User getUserByUsername(String username);

    /**
     * Lấy User theo email.
     * @param email email của người dùng
     * @return User nếu tìm thấy, null hoặc exception nếu không tìm thấy
     */
    User getUserByEmail(String email);

    /**
     * Lấy danh sách tất cả User.
     * @return danh sách tất cả user
     */
    List<User> getAllUsers();

    /**
     * Tạo mới User.
     * @param user đối tượng User cần tạo
     * @return User đã được tạo (có thể có ID mới...)
     */
    User createUser(User user);

    /**
     * Cập nhật User.
     * @param user đối tượng User đã được cập nhật thông tin
     * @return User đã được cập nhật
     */
    User updateUser(User user);

    /**
     * Xóa User theo ID.
     * @param userId ID người dùng cần xóa
     */
    void deleteUser(Long userId);

    /**
     * Kiểm tra tồn tại User theo username.
     * @param username username cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra tồn tại User theo email.
     * @param email email cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra tồn tại User theo ID.
     * @param id ID cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsById(Long id);
}
