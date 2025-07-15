package school.xxxx.domain.service.user;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import school.xxxx.domain.model.entity.User;

import java.util.List;

public interface UserDomainService {

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the User object if found, null otherwise
     */
    User getUserById(Long userId);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the User object if found, null otherwise
     */
    User getUserByUsername(String username);

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user to retrieve
     * @return the User object if found, null otherwise
     */
    User getUserByEmail(String email);

    /**
     * Retrieves all users.
     *
     * @return a list of all User objects
     */
    List<User> getAllUsers();

    /**
     * Creates a new user.
     *
     * @param user the User object to create
     * @return the created User object
     */
    User createUser(User user);

    /**
     * Updates an existing user.
     *
     * @param user the User object with updated information
     * @return the updated User object
     */
    User updateUser(User user);

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     */
    void deleteUser(Long userId);

    /**
     * Checks if a user exists by their username.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by their email.
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user exists by their ID.
     *
     * @param id the ID to check
     * @return true if a user with the given ID exists, false otherwise
     */
    boolean existsById(Long id);
}
