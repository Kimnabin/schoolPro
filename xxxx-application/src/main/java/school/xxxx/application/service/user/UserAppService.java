package school.xxxx.application.service.user;

import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
// DTO chứa dữ liệu từ client để tạo user mới
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
// DTO chứa dữ liệu update user
import school.xxxx.application.model.dto.user.response.PageResponse;
// DTO generic trả về kết quả phân trang
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
// DTO trả về thông tin user

import java.util.List;

public interface UserAppService {

    /**
     * Tạo user mới từ request DTO.
     *
     * @param userCreateReqDTO dữ liệu tạo user
     * @return UserResponseDTO thông tin user đã tạo
     */
    UserResponseDTO createNewUser(UserCreateReqDTO userCreateReqDTO);

    /**
     * Cập nhật user theo userId.
     *
     * @param userId ID user cần update
     * @param userUpdateReqDTO dữ liệu update (partial update)
     * @return UserResponseDTO sau khi update
     */
    UserResponseDTO updateUser(Long userId, UserUpdateReqDTO userUpdateReqDTO);

    /**
     * Lấy thông tin user theo ID.
     *
     * @param userId ID của user
     * @return UserResponseDTO nếu tìm thấy, hoặc null/ném exception nếu không tồn tại
     */
    UserResponseDTO getUserById(Long userId);

    /**
     * Xóa user theo ID.
     *
     * @param userId ID user cần xóa
     */
    void deleteUser(Long userId);

    /**
     * Tìm user theo username.
     *
     * @param username tên đăng nhập
     * @return UserResponseDTO nếu tìm thấy, hoặc null/ném exception nếu không tồn tại
     */
    UserResponseDTO getUserByUsername(String username);

    /**
     * Tìm user theo email.
     *
     * @param email email của user
     * @return UserResponseDTO nếu tìm thấy, hoặc null/ném exception nếu không tồn tại
     */
    UserResponseDTO getUserByEmail(String email);

    /**
     * Lấy toàn bộ danh sách user (KHÔNG phân trang).
     * -> Có thể gây nặng nếu số lượng user lớn.
     *
     * @return List<UserResponseDTO> toàn bộ user
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Lấy danh sách user có lọc + phân trang + sắp xếp.
     *
     * @param username lọc theo username (optional)
     * @param email lọc theo email (optional)
     * @param page số trang (zero-based)
     * @param size số phần tử mỗi trang
     * @param sortBy trường để sắp xếp
     * @param sortDirection hướng sắp xếp (asc/desc)
     * @return List<UserResponseDTO> danh sách user theo filter (KHÔNG trả metadata phân trang)
     */
    List<UserResponseDTO> listUsers(String username, String email, int page, int size, String sortBy, String sortDirection);

    // 👉 Nếu muốn trả về đầy đủ metadata phân trang (totalPages, totalElements, ...) thì dùng:
    // PageResponse<UserResponseDTO> listUsersWithPagination(...)

}
