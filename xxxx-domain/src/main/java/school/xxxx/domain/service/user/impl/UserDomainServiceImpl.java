package school.xxxx.domain.service.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.repositoty.user.UserRepository;
import school.xxxx.domain.service.user.UserDomainService;

import java.util.List;

@Service  // Đánh dấu đây là service component quản lý logic nghiệp vụ domain liên quan User
@RequiredArgsConstructor
@Slf4j
@Transactional()  // Chỉ đọc dữ liệu, không thay đổi DB trong các phương thức này
public class UserDomainServiceImpl implements UserDomainService {

    @Autowired  // Tự động inject implementation của UserRepository
    private UserRepository userRepository;

    /**
     * Lấy người dùng theo ID, nếu không tìm thấy sẽ ném IllegalArgumentException
     */
//    @Override
//    public User getUserById(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//    }
    @Cacheable(value = "users", key = "#id")
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    /**
     * Lấy người dùng theo username, ném lỗi nếu không tìm thấy
     */
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    /**
     * Lấy người dùng theo email, ném lỗi nếu không tìm thấy
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    /**
     * Lấy danh sách tất cả người dùng
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Tạo mới người dùng bằng cách gọi save của repository
     */
//    @Override
//    public User createUser(User user) {
//        return userRepository.save(user);
//    }
    @Transactional // Only for write operations
    @CacheEvict(value = "users", key = "#result.id")
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Cập nhật người dùng:
     * - Kiểm tra tồn tại user theo id trước khi update
     * - Nếu không tồn tại, ném RuntimeException
     * - Nếu tồn tại, gọi save để update (thường save sẽ xử lý insert/update tùy id)
     */
    @Override
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("Cannot update. User not found with id: " + user.getId());
        }
        return userRepository.save(user);
    }

    /**
     * Xóa người dùng theo ID
     */
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Kiểm tra tồn tại user theo username
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Kiểm tra tồn tại user theo email
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Kiểm tra tồn tại user theo ID
     */
    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
