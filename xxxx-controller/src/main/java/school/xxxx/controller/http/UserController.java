package school.xxxx.controller.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.application.service.user.UserAppService;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@Slf4j
public class UserController {

    @Autowired
    private UserAppService userAppService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userAppService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getUserById")
    public ResponseEntity<?> getUserById(@RequestParam Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID cannot be null");
        }
        UserResponseDTO user = userAppService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getUserByUsername")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be null or empty");
        }
        UserResponseDTO user = userAppService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getUserByEmail")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email cannot be null or empty");
        }
        UserResponseDTO user = userAppService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody UserCreateReqDTO user) {
        if (user == null || user.getUsername() == null || user.getEmail() == null) {
            return ResponseEntity.badRequest().body("User, username, and email cannot be null");
        }
        UserResponseDTO createdUser = userAppService.createNewUser(user);
        return ResponseEntity.ok(createdUser);
    }

//    @PutMapping("/updateUser")
//    public ResponseEntity<?> updateUser(@RequestParam Long userId,
//                                        @RequestParam String username,
//                                        @RequestParam String email) {
//        // Implement update logic in service and return updated user
//        UserResponseDTO updatedUser = userAppService.updateUser(userId, username, email);
//        return ResponseEntity.ok(updatedUser);
//    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam Long userId) {
        userAppService.deleteUser(userId);
        return ResponseEntity.ok("User deleted with ID: " + userId);
    }
}