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

@Service  // Đánh dấu class này là service trong Spring, để Spring tự động quản lý bean
@RequiredArgsConstructor // Lombok tự sinh constructor cho các biến final bên dưới (Dependency Injection)
public class UserAppServiceImpl implements UserAppService {

    // Inject service xử lý nghiệp vụ tầng domain (lớp logic chính liên quan User)
    private final UserDomainService userDomainService;

    // Mapper để chuyển đổi giữa DTO và Entity (User)
    private final UserMapper userMapper;

    // Mapper để chuyển danh sách User sang danh sách DTO (response)
    private final UserListMapper userListMapper;

    /**
     * Tạo mới một user
     * - nhận vào DTO chứa dữ liệu tạo user (UserCreateReqDTO)
     * - chuyển DTO sang entity User bằng userMapper
     * - gọi domain service để lưu entity User
     * - chuyển entity User lưu thành công sang DTO trả về cho client
     */
    @Override
    public UserResponseDTO createNewUser(UserCreateReqDTO userCreateReqDTO) {
        User user = userMapper.toEntity(userCreateReqDTO); // DTO -> Entity
        User saved = userDomainService.createUser(user); // lưu entity
        return userMapper.toDTO(saved); // Entity -> DTO
    }

    /**
     * Cập nhật thông tin user
     * - lấy user hiện tại theo userId (nếu không có sẽ ném exception ở domain service)
     * - cập nhật thông tin entity user bằng dữ liệu mới từ DTO (userUpdateReqDTO)
     * - gọi domain service để cập nhật entity User
     * - trả về DTO của user đã cập nhật
     */
    @Override
    public UserResponseDTO updateUser(Long userId, UserUpdateReqDTO userUpdateReqDTO) {
        User user = userDomainService.getUserById(userId); // lấy entity hiện tại
        userMapper.updateEntity(userUpdateReqDTO, user); // cập nhật dữ liệu entity với DTO
        User updated = userDomainService.updateUser(user); // cập nhật entity trong DB
        return userMapper.toDTO(updated); // trả về DTO
    }

    /**
     * Lấy user theo ID
     * - gọi domain service để lấy entity user theo userId
     * - chuyển entity sang DTO trả về
     */
    @Override
    public UserResponseDTO getUserById(Long userId) {
        return userMapper.toDTO(userDomainService.getUserById(userId));
    }

    /**
     * Xóa user theo userId
     * - gọi domain service để thực hiện xóa
     */
    @Override
    public void deleteUser(Long userId) {
        userDomainService.deleteUser(userId);
    }

    /**
     * Lấy user theo username
     * - gọi domain service lấy entity User theo username
     * - chuyển sang DTO trả về
     */
    @Override
    public UserResponseDTO getUserByUsername(String username) {
        return userMapper.toDTO(userDomainService.getUserByUsername(username));
    }

    /**
     * Lấy user theo email
     * - gọi domain service lấy entity User theo email
     * - chuyển sang DTO trả về
     */
    @Override
    public UserResponseDTO getUserByEmail(String email) {
        return userMapper.toDTO(userDomainService.getUserByEmail(email));
    }

    /**
     * Lấy danh sách tất cả user
     * - gọi domain service lấy list entity User
     * - chuyển danh sách entity sang list DTO trả về
     */
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userListMapper.toDTOList(userDomainService.getAllUsers());
    }

    /**
     * Lấy danh sách user với các tham số lọc, phân trang, sắp xếp
     * Hiện tại bạn đang trả về toàn bộ user mà chưa xử lý tham số
     * - username: lọc theo tên người dùng
     * - email: lọc theo email
     * - page, size: phân trang
     * - sortBy: trường sắp xếp
     * - sortDirection: hướng sắp xếp (ASC/DESC)
     */
    @Override
    public List<UserResponseDTO> listUsers(String username, String email, int page, int size, String sortBy, String sortDirection) {
        // TODO: cần gọi domain service xử lý lọc, phân trang, sắp xếp dựa trên các tham số trên
        return userListMapper.toDTOList(userDomainService.getAllUsers());
    }

}
