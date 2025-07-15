package school.xxxx.application.model.dto.user.response;

import lombok.Data;
// Lombok @Data: tự động sinh getter, setter, equals, hashCode, toString

import java.util.List;
// Dùng để chứa danh sách phần tử (generic)

@Data
public class PageResponse<T> {

    private List<T> content;
    // Danh sách dữ liệu của trang hiện tại (ví dụ: list UserResponseDTO)

    private int page;
    // Số trang hiện tại (zero-based index, tức page=0 là trang đầu tiên)

    private int size;
    // Số phần tử tối đa mỗi trang

    private long totalElements;
    // Tổng số phần tử trong toàn bộ dataset (VD: có tổng cộng 500 user)

    private int totalPages;
    // Tổng số trang (VD: 500 user với size=10 -> totalPages=50)

    private boolean last;
    // Có phải trang cuối không? (true nếu là trang cuối cùng)
}
