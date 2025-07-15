package school.xxxx.application.model.dto.user.request;

import jakarta.validation.constraints.Max;      // Ràng buộc giá trị tối đa
import jakarta.validation.constraints.Min;      // Ràng buộc giá trị tối thiểu
import jakarta.validation.constraints.Pattern;  // Ràng buộc chuỗi phải khớp regex
import lombok.Data; // Lombok tự động tạo getter, setter, equals, hashCode, toString

@Data
// Lombok @Data: tự động sinh getter, setter, equals, hashCode, toString
public class UserListReqDTO {

    private String username;
    // Bộ lọc tùy chọn (optional) theo username khi tìm kiếm danh sách User

    private String email;
    // Bộ lọc tùy chọn theo email

    @Min(value = 0, message = "Page number cannot be negative") // Số trang không được âm (page >= 0)
    private int page = 0;       // Mặc định = 0 (trang đầu tiên, theo kiểu zero-based index)

    @Min(value = 1, message = "Page size must be at least 1")       // Kích thước trang tối thiểu 1
    @Max(value = 100, message = "Page size cannot exceed 100")      // Kích thước trang tối đa 100 để tránh load quá nhiều dữ liệu
    private int size = 10;                                          // Mặc định = 10 (mỗi trang 10 phần tử)


    @Pattern(
            regexp = "^(id|username|email|fullName|createdAt)$",
            message = "Sort field must be one of: id, username, email, fullName, createdAt"
    )
    // Chỉ cho phép sắp xếp theo các trường hợp lệ: id, username, email, fullName, createdAt
    private String sortBy = "id";       // Mặc định sắp xếp theo id


    @Pattern(
            regexp = "^(asc|desc)$",
            message = "Sort direction must be either 'asc' or 'desc'"
    )
    // Chỉ cho phép giá trị asc (tăng dần) hoặc desc (giảm dần)
    private String sortDirection = "asc";
    // Mặc định sắp xếp tăng dần (asc)
}
