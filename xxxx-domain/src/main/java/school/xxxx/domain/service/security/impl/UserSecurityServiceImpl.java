package school.xxxx.domain.service.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.xxxx.domain.exception.user.UserNotFoundException;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.repositoty.user.UserRepository;
import school.xxxx.domain.service.security.UserSecurityService;

/**
 * Implementation cho UserSecurityService - chỉ phục vụ Security
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserSecurityServiceImpl implements UserSecurityService {

    private final UserRepository userRepository;

    @Override
    public User loadUserForAuthentication(String usernameOrEmail) {
        log.debug("Loading user for authentication: {}", usernameOrEmail);

        // Try username first
        if (userRepository.existsByUsername(usernameOrEmail)) {
            return userRepository.findActiveByUsername(usernameOrEmail)
                    .orElseThrow(() -> new UserNotFoundException("username", usernameOrEmail));
        }

        // Try email
        if (userRepository.existsByEmail(usernameOrEmail)) {
            return userRepository.findActiveByEmail(usernameOrEmail)
                    .orElseThrow(() -> new UserNotFoundException("email", usernameOrEmail));
        }

        throw new UserNotFoundException("username or email", usernameOrEmail);
    }

    @Override
    public User loadUserById(Long userId) {
        log.debug("Loading user by ID for security: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}