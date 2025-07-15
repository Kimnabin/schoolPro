package school.xxxx.domain.repositoty.user;

import school.xxxx.domain.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface định nghĩa các thao tác truy xuất dữ liệu người dùng (User)
 * Đây là tầng Repository trong mô hình Domain-driven design (DDD)
 * hoặc tương đương với DAO trong kiến trúc truyền thống.
 *
 * Lưu ý: Đây là interface, phần triển khai cụ thể sẽ được cung cấp bởi
 * framework hoặc tự cài đặt, ví dụ JPA Repository, MyBatis, v.v.
 */
public interface UserRepository {

    /**
     * Tìm người dùng theo username (unique)
     * @param username tên đăng nhập
     * @return Optional<User> có giá trị nếu tìm thấy, không có nếu không tìm thấy
     */
    Optional<User> findByUsername(String username);

    /**
     * Tìm người dùng theo email (unique)
     * @param email email của người dùng
     * @return Optional<User> có hoặc không
     */
    Optional<User> findByEmail(String email);

    /**
     * Tìm người dùng theo ID (primary key)
     * @param id ID người dùng
     * @return Optional<User>
     */
    Optional<User> findById(Long id);

    /**
     * Lấy danh sách tất cả người dùng
     * @return List<User> tất cả user trong DB
     */
    List<User> findAll();

    /**
     * Lưu người dùng vào DB, dùng cho tạo mới hoặc cập nhật
     * @param user đối tượng User cần lưu
     * @return User đã được lưu (có thể có ID mới, các trường tự động cập nhật...)
     */
    User save(User user);

    /**
     * Xóa người dùng theo ID
     * @param id ID người dùng cần xóa
     */
    void deleteById(Long id);

    /**
     * Kiểm tra sự tồn tại của user theo username
     * @param username username cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra sự tồn tại của user theo email
     * @param email email cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra sự tồn tại của user theo ID
     * @param id ID cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsById(Long id);

}
