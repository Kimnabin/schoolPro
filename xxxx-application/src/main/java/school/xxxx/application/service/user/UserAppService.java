package school.xxxx.application.service.user;

import org.springframework.data.domain.Page;
import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;

import java.util.List;

/**
 * Application Service for User operations
 * Acts as orchestrator between Controller and Domain Service
 *
 * @author Senior Backend Developer
 */
public interface UserAppService {

    /**
     * Get user by ID
     */
    UserResponseDTO getUserById(Long userId);

    /**
     * Get user by username
     */
    UserResponseDTO getUserByUsername(String username);

    /**
     * Get user by email
     */
    UserResponseDTO getUserByEmail(String email);

    /**
     * Get all users
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Create new user
     */
    UserResponseDTO createNewUser(UserCreateReqDTO createRequest);

    /**
     * Update existing user
     */
    UserResponseDTO updateUser(Long userId, UserUpdateReqDTO updateRequest);

    /**
     * Delete user by ID
     */
    void deleteUser(Long userId);

    /**
     * List users with filters and pagination
     */
    List<UserResponseDTO> listUsers(String username, String email, int page, int size,
                                    String sortBy, String sortDirection);

    /**
     * Search users with pagination
     */
    Page<UserResponseDTO> searchUsers(String keyword, int page, int size,
                                      String sortBy, String sortDirection);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}