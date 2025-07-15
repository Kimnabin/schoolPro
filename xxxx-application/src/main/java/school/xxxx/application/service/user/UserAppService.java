package school.xxxx.application.service.user;

import school.xxxx.application.model.dto.user.request.UserCreateReqDTO;
import school.xxxx.application.model.dto.user.request.UserUpdateReqDTO;
import school.xxxx.application.model.dto.user.response.PageResponse;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;

import java.util.List;


public interface UserAppService {

    UserResponseDTO createNewUser(UserCreateReqDTO userCreateReqDTO);

    UserResponseDTO updateUser(Long userId, UserUpdateReqDTO userUpdateReqDTO);

    UserResponseDTO getUserById(Long userId);

    void deleteUser(Long userId);

    UserResponseDTO getUserByUsername(String username);

    UserResponseDTO getUserByEmail(String email);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> listUsers(String username, String email, int page, int size, String sortBy, String sortDirection);
//    PageResponse<UserResponseDTO> listUsersWithPagination(String username, String email, int page, int size, String sortBy, String sortDirection);



}
