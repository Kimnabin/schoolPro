package school.xxxx.infrastructure.persistence.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.repositoty.user.UserRepository;
import school.xxxx.infrastructure.persistence.mapper.user.UserJPAMapper;

import java.util.List;
import java.util.Optional;

/**
 * Lớp triển khai interface UserRepository, đóng vai trò Repository thực thi
 * sử dụng Spring Data JPA qua UserJPAMapper.
 *
 * Đây là tầng persistence, chịu trách nhiệm truy xuất dữ liệu thực tế với DB.
 */
@Repository  // Đánh dấu Spring quản lý bean này như 1 Repository component
@RequiredArgsConstructor  // Tự động sinh constructor với các field final để injection
@Slf4j  // Sinh logger dùng cho logging thông tin
public class UserRepoImfrasImpl implements UserRepository {

    // Mapper JPA thực tế xử lý CRUD trên entity User
    private final UserJPAMapper userJPAMapper;

    /**
     * Tìm User theo username
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userJPAMapper.findByUsername(username);
    }

    /**
     * Tìm User theo email
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userJPAMapper.findByEmail(email);
    }

    /**
     * Tìm User theo ID
     */
    @Override
    public Optional<User> findById(Long id) {
        return userJPAMapper.findById(id);
    }

    /**
     * Lấy tất cả User
     */
    @Override
    public List<User> findAll() {
        return userJPAMapper.findAll();
    }

    /**
     * Lưu hoặc cập nhật User
     */
    @Override
    public User save(User user) {
        return userJPAMapper.save(user);
    }

    /**
     * Xóa User theo ID và ghi log
     */
    @Override
    public void deleteById(Long id) {
        userJPAMapper.deleteById(id);
        log.info("Deleted user with ID: {}", id);
    }

    /**
     * Kiểm tra tồn tại User theo username
     */
    @Override
    public boolean existsByUsername(String username) {
        return userJPAMapper.existsByUsername(username);
    }

    /**
     * Kiểm tra tồn tại User theo email
     */
    @Override
    public boolean existsByEmail(String email) {
        return userJPAMapper.existsByEmail(email);
    }

    /**
     * Kiểm tra tồn tại User theo ID
     */
    @Override
    public boolean existsById(Long id) {
        return userJPAMapper.existsById(id);
    }

}
