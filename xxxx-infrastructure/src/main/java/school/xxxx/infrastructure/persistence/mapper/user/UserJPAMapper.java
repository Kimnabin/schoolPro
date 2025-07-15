package school.xxxx.infrastructure.persistence.mapper.user;

import org.springframework.data.jpa.repository.JpaRepository;
import school.xxxx.domain.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserJPAMapper extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
