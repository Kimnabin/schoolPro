package school.xxxx.controller.http;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.application.service.user.UserAppService;
import school.xxxx.controller.model.enums.ResultCode;
import school.xxxx.controller.model.enums.ResultUtil;
import school.xxxx.controller.model.vo.ResultMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")  // Đường dẫn gốc cho User API
@RequiredArgsConstructor           // Lombok tự sinh constructor cho biến final (userAppService)
@Slf4j                           // Lombok tạo logger để log thông tin
@Validated                      // Kích hoạt validaton cho các @NotNull, @NotBlank ...
public class UserController {

    private final UserAppService userAppService;  // Service tầng application xử lý logic user

    /**
     * Lấy danh sách tất cả người dùng
     * GET /api/v1/users
     */
    @GetMapping
    public ResponseEntity<ResultMessage<List<UserResponseDTO>>> getAllUsers() {
        try {
            log.info("Fetching all users");
            List<UserResponseDTO> users = userAppService.getAllUsers();
            return ResponseEntity.ok(ResultUtil.zdata(users));  // Trả về 200 OK với dữ liệu chuẩn
        } catch (Exception e) {
            log.error("Error fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(ResultCode.ERROR));  // Trả lỗi 500 nếu có sự cố
        }
    }

    /**
     * Lấy thông tin người dùng theo ID
     * GET /api/v1/users/{userId}
     * @param userId ID người dùng, bắt buộc không null
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ResultMessage<UserResponseDTO>> getUserById(
            @PathVariable("userId") @NotNull Long userId) {
        try {
            log.info("Fetching user with ID: {}", userId);
            UserResponseDTO user = userAppService.getUserById(userId);
            return ResponseEntity.ok(ResultUtil.zdata(user));
        } catch (Exception e) {
            log.error("Error fetching user with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND));  // Trả lỗi 404 nếu không tìm thấy
        }
    }

    /**
     * Lấy thông tin người dùng theo username
     * GET /api/v1/users/username/{username}
     * @param username Chuỗi username, không được rỗng
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ResultMessage<UserResponseDTO>> getUserByUsername(
            @PathVariable("username") @NotBlank String username) {
        try {
            log.info("Fetching user with username: {}", username);
            UserResponseDTO user = userAppService.getUserByUsername(username);
            return ResponseEntity.ok(ResultUtil.zdata(user));
        } catch (Exception e) {
            log.error("Error fetching user with username: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND));
        }
    }

    /**
     * Lấy thông tin người dùng theo email
     * GET /api/v1/users/email/{email}
     * @param email Chuỗi email, không được rỗng
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ResultMessage<UserResponseDTO>> getUserByEmail(
            @PathVariable("email") @NotBlank String email) {
        try {
            log.info("Fetching user with email: {}", email);
            UserResponseDTO user = userAppService.getUserByEmail(email);
            return ResponseEntity.ok(ResultUtil.zdata(user));
        } catch (Exception e) {
            log.error("Error fetching user with email: {}", email, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND));
        }
    }

    /**
     * Tạo mới người dùng
     * POST /api/v1/users/createUser
     * @param userCreateReqDTO DTO chứa thông tin tạo user, được validate
     */
    @PostMapping("createUser")
    public ResponseEntity<ResultMessage<UserResponseDTO>> createUser(
            @Valid @RequestBody UserCreateReqDTO userCreateReqDTO) {
        try {
            log.info("Creating new user with username: {}", userCreateReqDTO.getUsername());
            UserResponseDTO createdUser = userAppService.createNewUser(userCreateReqDTO);
            return ResponseEntity.status(HttpStatus.CREATED)  // Trả về 201 Created
                    .body(ResultUtil.zdata(createdUser));
        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResultUtil.error(ResultCode.PARAMS_ERROR));  // Tham số không hợp lệ trả lỗi 400
        }
    }

    /**
     * Cập nhật thông tin người dùng
     * PUT /api/v1/users/{userId}
     * @param userId ID người dùng cần cập nhật
     * @param userUpdateReqDTO DTO dữ liệu cập nhật đã validate
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ResultMessage<UserResponseDTO>> updateUser(
            @PathVariable("userId") @NotNull Long userId,
            @Valid @RequestBody UserUpdateReqDTO userUpdateReqDTO) {
        try {
            log.info("Updating user with ID: {}", userId);
            // Đảm bảo id trong body bằng id trong đường dẫn
            userUpdateReqDTO.setId(userId);
            UserResponseDTO updatedUser = userAppService.updateUser(userId, userUpdateReqDTO);
            return ResponseEntity.ok(ResultUtil.zdata(updatedUser));
        } catch (Exception e) {
            log.error("Error updating user with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND));
        }
    }

    /**
     * Xóa người dùng theo ID
     * DELETE /api/v1/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResultMessage<String>> deleteUser(
            @PathVariable("userId") @NotNull Long userId) {
        try {
            log.info("Deleting user with ID: {}", userId);
            userAppService.deleteUser(userId);
            return ResponseEntity.ok(ResultUtil.zdata("User deleted successfully with ID: " + userId));
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND));
        }
    }

    /**
     * Tìm kiếm người dùng với phân trang
     * GET /api/v1/users/search?username=...&email=...&page=...&size=...&sortBy=...&sortDirection=...
     */
    @GetMapping("/search")
    public ResponseEntity<ResultMessage<List<UserResponseDTO>>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            log.info("Searching users with filters - username: {}, email: {}, page: {}, size: {}",
                    username, email, page, size);
            List<UserResponseDTO> users = userAppService.listUsers(username, email, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ResultUtil.zdata(users));
        } catch (Exception e) {
            log.error("Error searching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(ResultCode.ERROR));
        }
    }
}
