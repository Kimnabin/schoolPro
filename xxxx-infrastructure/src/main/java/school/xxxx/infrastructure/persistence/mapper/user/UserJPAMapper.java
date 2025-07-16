package school.xxxx.infrastructure.persistence.mapper.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.xxxx.domain.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface JpaRepository cung cấp sẵn các phương thức CRUD và paging/sorting.
 *
 * UserJPAMapper kế thừa JpaRepository<User, Long> để sử dụng các phương thức thao tác
 * cơ sở dữ liệu với entity User có kiểu khóa chính là Long.
 *
 * Đây chính là lớp Repository thực thi việc truy xuất dữ liệu thực tế sử dụng JPA.
 */
public interface UserJPAMapper extends JpaRepository<User, Long> {

    /**
     * Tìm User theo username (unique)
     * @param username username của user
     * @return Optional<User> có giá trị nếu tìm thấy, Optional.empty() nếu không
     */
    Optional<User> findByUsername(String username);

    /**
     * Tìm User theo email (unique)
     * @param email email của user
     * @return Optional<User> có giá trị nếu tìm thấy, Optional.empty() nếu không
     */
    Optional<User> findByEmail(String email);

    /**
     * Kiểm tra sự tồn tại User theo username
     * @param username username cần kiểm tra
     * @return true nếu tồn tại user với username đó, false nếu không
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra sự tồn tại User theo email
     * @param email email cần kiểm tra
     * @return true nếu tồn tại user với email đó, false nếu không
     */
    boolean existsByEmail(String email);

    // UserJPAMapper.java - Thêm queries tối ưu
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.state = true")
    Optional<User> findActiveByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE (:username IS NULL OR u.username LIKE %:username%) " +
            "AND (:email IS NULL OR u.email LIKE %:email%)")
    Page<User> findUsersWithFilters(@Param("username") String username,
                                    @Param("email") String email,
                                    Pageable pageable);



}
