package school.xxxx.domain.service.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import school.xxxx.domain.exception.user.UserNotFoundException;
import school.xxxx.domain.exception.user.UserAlreadyExistsException;
import school.xxxx.domain.exception.user.InvalidUserDataException;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.repositoty.user.UserRepository;
import school.xxxx.domain.service.user.UserDomainService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDomainServiceImpl implements UserDomainService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "users", key = "#id")
    @Override
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidUserDataException("id", "must be a positive number");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidUserDataException("username", "cannot be null or empty");
        }

        return userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UserNotFoundException("username", username));
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserDataException("email", "cannot be null or empty");
        }

        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "users", key = "#result.id")
    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new InvalidUserDataException("User data cannot be null");
        }

        // Validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new InvalidUserDataException("username", "cannot be null or empty");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new InvalidUserDataException("email", "cannot be null or empty");
        }

        // Check for existing username
        if (userRepository.existsByUsername(user.getUsername().trim())) {
            throw new UserAlreadyExistsException("username", user.getUsername());
        }

        // Check for existing email
        if (userRepository.existsByEmail(user.getEmail().trim().toLowerCase())) {
            throw new UserAlreadyExistsException("email", user.getEmail());
        }

        // Normalize data before saving
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());

        log.info("Creating new user with username: {}", user.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#user.id")
    @Override
    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new InvalidUserDataException("User ID cannot be null for update operation");
        }

        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }

        // Check for existing email if email is being changed
        if (user.getEmail() != null) {
            User existingUser = userRepository.findById(user.getId()).orElse(null);
            if (existingUser != null && !existingUser.getEmail().equals(user.getEmail().trim().toLowerCase())) {
                if (userRepository.existsByEmail(user.getEmail().trim().toLowerCase())) {
                    throw new UserAlreadyExistsException("email", user.getEmail());
                }
            }
            user.setEmail(user.getEmail().trim().toLowerCase());
        }

        log.info("Updating user with ID: {}", user.getId());
        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    @Override
    public void deleteUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new InvalidUserDataException("id", "must be a positive number");
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        log.info("Deleting user with ID: {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByUsername(username.trim());
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return userRepository.existsById(id);
    }
}
