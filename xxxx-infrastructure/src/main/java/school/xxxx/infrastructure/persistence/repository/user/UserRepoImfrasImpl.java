package school.xxxx.infrastructure.persistence.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.repositoty.user.UserRepository;
import school.xxxx.infrastructure.persistence.mapper.user.UserJPAMapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepoImfrasImpl implements UserRepository {

    private final UserJPAMapper userJPAMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJPAMapper.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJPAMapper.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJPAMapper.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userJPAMapper.findAll();
    }

    @Override
    public User save(User user) {
        return userJPAMapper.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userJPAMapper.deleteById(id);
        log.info("Deleted user with ID: {}", id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJPAMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJPAMapper.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return userJPAMapper.existsById(id);
    }
}
