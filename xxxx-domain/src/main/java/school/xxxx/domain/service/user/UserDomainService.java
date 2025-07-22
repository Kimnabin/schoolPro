package school.xxxx.domain.service.user;

import school.xxxx.domain.model.entity.User;
import java.util.List;

/**
 * Interface định nghĩa các nghiệp vụ liên quan đến User trong tầng Domain Service.
 * CHỈ chứa core business logic, KHÔNG chứa authentication logic
 */
public interface UserDomainService {

    /**
     * Lấy User theo ID.
     */
    User getUserById(Long userId);

    /**
     * Lấy User theo username.
     */
    User getUserByUsername(String username);

    /**
     * Lấy User theo email.
     */
    User getUserByEmail(String email);

    /**
     * Lấy danh sách tất cả User.
     */
    List<User> getAllUsers();

    /**
     * Tạo mới User.
     */
    User createUser(User user);

    /**
     * Cập nhật User.
     */
    User updateUser(User user);

    /**
     * Xóa User theo ID.
     */
    void deleteUser(Long userId);

    /**
     * Kiểm tra tồn tại User theo username.
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra tồn tại User theo email.
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra tồn tại User theo ID.
     */
    boolean existsById(Long id);

    // ❌ LOẠI BỎ authentication methods - chuyển sang AuthAppService:
    // - authenticateUser
    // - changePassword
    // - resetPassword
    // - lockAccount
    // - unlockAccount
}