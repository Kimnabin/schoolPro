package school.xxxx.domain.service.user;

import school.xxxx.domain.model.entity.User;
import java.util.List;

/**
 * Interface định nghĩa các nghiệp vụ liên quan đến User trong tầng Domain Service.
 * Đây là nơi tập trung xử lý logic nghiệp vụ trước khi gọi Repository thao tác dữ liệu.
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

    /**
     * Authenticate user với username/email và password
     */
    User authenticateUser(String usernameOrEmail, String password);

    /**
     * Change user password với current password verification
     */
    void changePassword(Long userId, String currentPassword, String newPassword);

    /**
     * Reset user password (admin operation)
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * Lock user account
     */
    void lockAccount(Long userId, String reason);

    /**
     * Unlock user account
     */
    void unlockAccount(Long userId);
}