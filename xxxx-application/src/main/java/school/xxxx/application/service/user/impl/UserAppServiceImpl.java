package school.xxxx.application.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
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
public class UserAppServiceImpl implements UserAppService {

    private final UserDomainService userDomainService;
    private final UserMapper userMapper;
    private final UserListMapper userListMapper;

    @Override
    public UserResponseDTO createNewUser(UserCreateReqDTO userCreateReqDTO) {
        User user = userMapper.toEntity(userCreateReqDTO);
        User saved = userDomainService.createUser(user);
        return userMapper.toDTO(saved);
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserUpdateReqDTO userUpdateReqDTO) {
        User user = userDomainService.getUserById(userId);
        userMapper.updateEntity(userUpdateReqDTO, user);
        User updated = userDomainService.updateUser(user);
        return userMapper.toDTO(updated);
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        return userMapper.toDTO(userDomainService.getUserById(userId));
    }

    @Override
    public void deleteUser(Long userId) {
        userDomainService.deleteUser(userId);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        return userMapper.toDTO(userDomainService.getUserByUsername(username));
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        return userMapper.toDTO(userDomainService.getUserByEmail(email));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userListMapper.toDTOList(userDomainService.getAllUsers());
    }

    @Override
    public List<UserResponseDTO> listUsers(String username, String email, int page, int size, String sortBy, String sortDirection) {
        return userListMapper.toDTOList(userDomainService.getAllUsers());
    }


}