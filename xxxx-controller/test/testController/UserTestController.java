package school.xxxx.controller.http.testController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.application.service.user.UserAppService;
import school.xxxx.controller.model.enums.ResultCode;
import school.xxxx.controller.model.enums.ResultUtil;
import school.xxxx.controller.model.vo.ResultMessage;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/test/users")
@RequiredArgsConstructor
@Slf4j
public class UserTestController {

    private final UserAppService userAppService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Test Suite Dashboard - Danh sách tất cả test cases
     * GET /api/v1/test/users
     */
    @GetMapping
    public ResponseEntity<ResultMessage<Map<String, Object>>> getTestDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("title", "User Management Test Suite");
        dashboard.put("timestamp", LocalDateTime.now());

        List<Map<String, String>> testCases = Arrays.asList(
                Map.of("method", "POST", "endpoint", "/api/v1/test/users/create-sample", "description", "Tạo user mẫu"),
                Map.of("method", "GET", "endpoint", "/api/v1/test/users/all", "description", "Lấy tất cả users"),
                Map.of("method", "GET", "endpoint", "/api/v1/test/users/1", "description", "Lấy user theo ID"),
                Map.of("method", "GET", "endpoint", "/api/v1/test/users/username/testuser", "description", "Lấy user theo username"),
                Map.of("method", "GET", "endpoint", "/api/v1/test/users/email/test@example.com", "description", "Lấy user theo email"),
                Map.of("method", "PUT", "endpoint", "/api/v1/test/users/1", "description", "Cập nhật user"),
                Map.of("method", "DELETE", "endpoint", "/api/v1/test/users/1", "description", "Xóa user"),
                Map.of("method", "GET", "endpoint", "/api/v1/test/users/search", "description", "Tìm kiếm với filter"),
                Map.of("method", "POST", "endpoint", "/api/v1/test/users/bulk-create", "description", "Tạo nhiều users"),
                Map.of("method", "GET", "endpoint", "/api/v1/test/users/password-test", "description", "Test password encoding")
        );

        dashboard.put("available_tests", testCases);
        dashboard.put("total_tests", testCases.size());

        return ResponseEntity.ok(ResultUtil.zdata(dashboard));
    }

    /**
     * TEST 1: Tạo user mẫu
     * POST /api/v1/test/users/create-sample
     */
    @PostMapping("/create-sample")
    public ResponseEntity<ResultMessage<UserResponseDTO>> createSampleUser() {
        try {
            UserCreateReqDTO dto = new UserCreateReqDTO();
            dto.setUsername("testuser_" + System.currentTimeMillis());
            dto.setPassword("TestPass123!");
            dto.setFullName("Test User");
            dto.setEmail("test_" + System.currentTimeMillis() + "@example.com");
            dto.setPhoneNumber("0123456789");
            dto.setAddress("123 Test Street, Test City");
            dto.setState(true);

            log.info("Creating sample user: {}", dto.getUsername());
            UserResponseDTO result = userAppService.createNewUser(dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.zdata(result));
        } catch (Exception e) {
            log.error("Error creating sample user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "Failed to create sample user: " + e.getMessage()));
        }
    }

    /**
     * TEST 2: Tạo nhiều users cùng lúc
     * POST /api/v1/test/users/bulk-create
     */
    @PostMapping("/bulk-create")
    public ResponseEntity<ResultMessage<List<UserResponseDTO>>> bulkCreateUsers(
            @RequestParam(defaultValue = "5") int count) {
        try {
            List<UserResponseDTO> createdUsers = new ArrayList<>();
            long timestamp = System.currentTimeMillis();

            for (int i = 1; i <= count; i++) {
                UserCreateReqDTO dto = new UserCreateReqDTO();
                dto.setUsername("bulkuser_" + timestamp + "_" + i);
                dto.setPassword("BulkPass123!");
                dto.setFullName("Bulk User " + i);
                dto.setEmail("bulk_" + timestamp + "_" + i + "@example.com");
                dto.setPhoneNumber("012345678" + i);
                dto.setAddress(i + " Bulk Street, Bulk City");
                dto.setState(i % 2 == 0); // Alternate true/false

                UserResponseDTO result = userAppService.createNewUser(dto);
                createdUsers.add(result);
            }

            log.info("Bulk created {} users", count);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtil.zdata(createdUsers));
        } catch (Exception e) {
            log.error("Error bulk creating users", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResultUtil.error(ResultCode.PARAMS_ERROR.code(), "Bulk creation failed: " + e.getMessage()));
        }
    }

    /**
     * TEST 3: Lấy tất cả users
     * GET /api/v1/test/users/all
     */
    @GetMapping("/all")
    public ResponseEntity<ResultMessage<Map<String, Object>>> getAllUsersTest() {
        try {
            List<UserResponseDTO> users = userAppService.getAllUsers();

            Map<String, Object> result = new HashMap<>();
            result.put("users", users);
            result.put("total_count", users.size());
            result.put("timestamp", LocalDateTime.now());

            log.info("Retrieved {} users", users.size());
            return ResponseEntity.ok(ResultUtil.zdata(result));
        } catch (Exception e) {
            log.error("Error getting all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(ResultCode.ERROR));
        }
    }

    /**
     * TEST 4: Lấy user theo ID
     * GET /api/v1/test/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ResultMessage<UserResponseDTO>> getUserByIdTest(@PathVariable Long userId) {
        try {
            log.info("Testing get user by ID: {}", userId);
            UserResponseDTO user = userAppService.getUserById(userId);
            return ResponseEntity.ok(ResultUtil.zdata(user));
        } catch (Exception e) {
            log.error("Error getting user by ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "User not found with ID: " + userId));
        }
    }

    /**
     * TEST 5: Lấy user theo username
     * GET /api/v1/test/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ResultMessage<UserResponseDTO>> getUserByUsernameTest(@PathVariable String username) {
        try {
            log.info("Testing get user by username: {}", username);
            UserResponseDTO user = userAppService.getUserByUsername(username);
            return ResponseEntity.ok(ResultUtil.zdata(user));
        } catch (Exception e) {
            log.error("Error getting user by username: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "User not found with username: " + username));
        }
    }

    /**
     * TEST 6: Lấy user theo email
     * GET /api/v1/test/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ResultMessage<UserResponseDTO>> getUserByEmailTest(@PathVariable String email) {
        try {
            log.info("Testing get user by email: {}", email);
            UserResponseDTO user = userAppService.getUserByEmail(email);
            return ResponseEntity.ok(ResultUtil.zdata(user));
        } catch (Exception e) {
            log.error("Error getting user by email: {}", email, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "User not found with email: " + email));
        }
    }

    /**
     * TEST 7: Cập nhật user
     * PUT /api/v1/test/users/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ResultMessage<UserResponseDTO>> updateUserTest(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateReqDTO updateDTO) {
        try {
            log.info("Testing update user ID: {} with data: {}", userId, updateDTO);
            UserResponseDTO updatedUser = userAppService.updateUser(userId, updateDTO);
            return ResponseEntity.ok(ResultUtil.zdata(updatedUser));
        } catch (Exception e) {
            log.error("Error updating user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Update failed: " + e.getMessage()));
        }
    }

    /**
     * TEST 8: Xóa user
     * DELETE /api/v1/test/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResultMessage<String>> deleteUserTest(@PathVariable Long userId) {
        try {
            log.info("Testing delete user ID: {}", userId);
            userAppService.deleteUser(userId);
            return ResponseEntity.ok(ResultUtil.zdata("User deleted successfully with ID: " + userId));
        } catch (Exception e) {
            log.error("Error deleting user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), "Delete failed: " + e.getMessage()));
        }
    }

    /**
     * TEST 9: Tìm kiếm users với filter
     * GET /api/v1/test/users/search?username=test&email=example&page=0&size=5
     */
    @GetMapping("/search")
    public ResponseEntity<ResultMessage<Map<String, Object>>> searchUsersTest(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            log.info("Testing search users with filters - username: {}, email: {}, page: {}, size: {}",
                    username, email, page, size);

            List<UserResponseDTO> users = userAppService.listUsers(username, email, page, size, sortBy, sortDirection);

            Map<String, Object> result = new HashMap<>();
            result.put("users", users);
            result.put("search_params", Map.of(
                    "username", username != null ? username : "null",
                    "email", email != null ? email : "null",
                    "page", page,
                    "size", size,
                    "sortBy", sortBy,
                    "sortDirection", sortDirection
            ));
            result.put("result_count", users.size());
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(ResultUtil.zdata(result));
        } catch (Exception e) {
            log.error("Error searching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(ResultCode.ERROR.code(), "Search failed: " + e.getMessage()));
        }
    }

    /**
     * TEST 10: Test password encoding
     * GET /api/v1/test/users/password-test
     */
    @GetMapping("/password-test")
    public ResponseEntity<ResultMessage<Map<String, Object>>> passwordEncodingTest() {
        try {
            String plainPassword = "TestPassword123!";

            // Encode cùng password nhiều lần để show salt randomization
            List<String> encodedPasswords = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                encodedPasswords.add(passwordEncoder.encode(plainPassword));
            }

            // Test verification
            String hashedPassword = encodedPasswords.get(0);
            boolean correctPasswordMatches = passwordEncoder.matches(plainPassword, hashedPassword);
            boolean wrongPasswordMatches = passwordEncoder.matches("WrongPassword", hashedPassword);

            Map<String, Object> result = new HashMap<>();
            result.put("plain_password", plainPassword);
            result.put("encoded_passwords", encodedPasswords);
            result.put("note", "Notice how same password produces different hashes due to salt");
            result.put("correct_password_matches", correctPasswordMatches);
            result.put("wrong_password_matches", wrongPasswordMatches);
            result.put("encoder_info", passwordEncoder.getClass().getSimpleName());

            return ResponseEntity.ok(ResultUtil.zdata(result));
        } catch (Exception e) {
            log.error("Error testing password encoding", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(ResultCode.ERROR.code(), "Password test failed: " + e.getMessage()));
        }
    }

    /**
     * TEST 11: Validation test - Tạo user với dữ liệu không hợp lệ
     * POST /api/v1/test/users/validation-test
     */
    @PostMapping("/validation-test")
    public ResponseEntity<ResultMessage<Map<String, Object>>> validationTest() {
        Map<String, Object> testResults = new HashMap<>();

        // Test case 1: Username quá ngắn
        try {
            UserCreateReqDTO invalidUser1 = new UserCreateReqDTO();
            invalidUser1.setUsername("ab"); // Quá ngắn (min=3)
            invalidUser1.setPassword("ValidPass123!");
            invalidUser1.setFullName("Test User");
            invalidUser1.setEmail("test@example.com");

            userAppService.createNewUser(invalidUser1);
            testResults.put("short_username_test", "FAILED - Should have thrown validation error");
        } catch (Exception e) {
            testResults.put("short_username_test", "PASSED - Caught validation error: " + e.getMessage());
        }

        // Test case 2: Email không hợp lệ
        try {
            UserCreateReqDTO invalidUser2 = new UserCreateReqDTO();
            invalidUser2.setUsername("validuser");
            invalidUser2.setPassword("ValidPass123!");
            invalidUser2.setFullName("Test User");
            invalidUser2.setEmail("invalid-email"); // Email không hợp lệ

            userAppService.createNewUser(invalidUser2);
            testResults.put("invalid_email_test", "FAILED - Should have thrown validation error");
        } catch (Exception e) {
            testResults.put("invalid_email_test", "PASSED - Caught validation error: " + e.getMessage());
        }

        // Test case 3: Password yếu
        try {
            UserCreateReqDTO invalidUser3 = new UserCreateReqDTO();
            invalidUser3.setUsername("validuser2");
            invalidUser3.setPassword("123"); // Password quá yếu
            invalidUser3.setFullName("Test User");
            invalidUser3.setEmail("test2@example.com");

            userAppService.createNewUser(invalidUser3);
            testResults.put("weak_password_test", "FAILED - Should have thrown validation error");
        } catch (Exception e) {
            testResults.put("weak_password_test", "PASSED - Caught validation error: " + e.getMessage());
        }

        testResults.put("test_summary", "Validation tests completed");
        testResults.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(ResultUtil.zdata(testResults));
    }

    /**
     * TEST 12: Performance test - Đo thời gian thực hiện operations
     * GET /api/v1/test/users/performance-test
     */
    @GetMapping("/performance-test")
    public ResponseEntity<ResultMessage<Map<String, Object>>> performanceTest() {
        Map<String, Object> results = new HashMap<>();

        try {
            // Test tạo user
            long startTime = System.currentTimeMillis();
            UserCreateReqDTO dto = new UserCreateReqDTO();
            dto.setUsername("perftest_" + System.currentTimeMillis());
            dto.setPassword("PerfTest123!");
            dto.setFullName("Performance Test User");
            dto.setEmail("perftest_" + System.currentTimeMillis() + "@example.com");
            dto.setPhoneNumber("0987654321");
            dto.setAddress("Performance Test Address");
            dto.setState(true);

            UserResponseDTO createdUser = userAppService.createNewUser(dto);
            long createTime = System.currentTimeMillis() - startTime;
            results.put("create_user_time_ms", createTime);

            // Test lấy user by ID
            startTime = System.currentTimeMillis();
            userAppService.getUserById(createdUser.getId());
            long getByIdTime = System.currentTimeMillis() - startTime;
            results.put("get_by_id_time_ms", getByIdTime);

            // Test lấy tất cả users
            startTime = System.currentTimeMillis();
            List<UserResponseDTO> allUsers = userAppService.getAllUsers();
            long getAllTime = System.currentTimeMillis() - startTime;
            results.put("get_all_users_time_ms", getAllTime);
            results.put("total_users_count", allUsers.size());

            // Test cập nhật user
            UserUpdateReqDTO updateDto = new UserUpdateReqDTO();
            updateDto.setFullName("Updated Performance Test User");
            updateDto.setAddress("Updated Address");

            startTime = System.currentTimeMillis();
            userAppService.updateUser(createdUser.getId(), updateDto);
            long updateTime = System.currentTimeMillis() - startTime;
            results.put("update_user_time_ms", updateTime);

            // Test xóa user
            startTime = System.currentTimeMillis();
            userAppService.deleteUser(createdUser.getId());
            long deleteTime = System.currentTimeMillis() - startTime;
            results.put("delete_user_time_ms", deleteTime);

            results.put("performance_summary", "All operations completed successfully");
            results.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            log.error("Performance test failed", e);
            results.put("error", "Performance test failed: " + e.getMessage());
        }

        return ResponseEntity.ok(ResultUtil.zdata(results));
    }
}