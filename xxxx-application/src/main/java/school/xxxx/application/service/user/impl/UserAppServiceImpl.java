package school.xxxx.application.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.xxxx.application.mapper.user.PagedResponseMapper;
import school.xxxx.application.mapper.user.UserListMapper;
import school.xxxx.application.mapper.user.UserMapper;
import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.application.service.user.UserAppService;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.service.user.UserDomainService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserAppServiceImpl implements UserAppService {

    private final UserDomainService userDomainService;
    private final UserMapper userMapper;
    private final UserListMapper userListMapper;
    private final PagedResponseMapper pagedResponseMapper;


    @Override
    public UserResponseDTO getUserById(Long userId) {
        log.debug("Getting user by ID: {}", userId);
        User user = userDomainService.getUserById(userId);
        return userMapper.toDTO(user);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        User user = userDomainService.getUserByUsername(username);
        return userMapper.toDTO(user);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        User user = userDomainService.getUserByEmail(email);
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Getting all users");
        List<User> users = userDomainService.getAllUsers();
        return userListMapper.toDTOList(users);    }

    @Override
    @Transactional
    public UserResponseDTO createNewUser(UserCreateReqDTO createRequest) {
        log.info("Creating new user with username: {}", createRequest.getUsername());

        // Convert DTO to Entity
        User user = userMapper.toEntity(createRequest);

        // Create user through domain service
        User createdUser = userDomainService.createUser(user);

        // Convert back to DTO and return
        return userMapper.toDTO(createdUser);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateReqDTO updateRequest) {
        log.info("Updating user with ID: {}", userId);

        // Get existing user
        User existingUser = userDomainService.getUserById(userId);

        // Update fields from DTO
        userMapper.updateEntity(updateRequest, existingUser);

        // Save through domain service
        User updatedUser = userDomainService.updateUser(existingUser);

        // Convert to DTO and return
        return userMapper.toDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        userDomainService.deleteUser(userId);
    }

    @Override
    public List<UserResponseDTO> listUsers(String username, String email, int page, int size, String sortBy, String sortDirection) {
        log.debug("Listing users with filters - username: {}, email: {}, page: {}, size: {}",
                username, email, page, size);

        // For now, just get all users (simplified)
        // In a real implementation, you'd pass filters to domain service
        List<User> users = userDomainService.getAllUsers();

        // Convert to DTOs
        return userListMapper.toDTOList(users);
    }

    @Override
    public Page<UserResponseDTO> searchUsers(String keyword, int page, int size, String sortBy, String sortDirection) {
        log.debug("Searching users with keyword: {}", keyword);

        // Create Pageable
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // For now, simplified implementation
        // In real app, implement search in domain service
        List<User> users = userDomainService.getAllUsers();

        // Convert to Page<UserResponseDTO> (simplified implementation)
        return Page.empty(pageable);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDomainService.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDomainService.existsByEmail(email);
    }
}
