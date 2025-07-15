package school.xxxx.application.mapper.user;

import org.springframework.data.domain.Page;
// Page là interface của Spring Data đại diện cho kết quả phân trang (paging result)
import org.springframework.stereotype.Component;
// @Component để Spring quản lý Bean này tự động
import school.xxxx.application.model.dto.user.response.PageResponse;
// Đây là DTO response trả về kết quả phân trang

import java.util.List;
import java.util.function.Function;
// Function<E, D> là functional interface dùng để chuyển đổi từ entity sang DTO
import java.util.stream.Collectors;
// Collectors để thu thập kết quả stream thành List

@Component
// Đánh dấu class này là Spring Bean, có thể inject vào nơi khác
public class PagedResponseMapper {

    /**
     * Phương thức generic để chuyển đổi kết quả phân trang (Page<E>)
     * thành một DTO phân trang (PageResponse<D>).
     *
     * @param entityPage: kết quả phân trang chứa danh sách entity (E)
     * @param converter: function để chuyển đổi entity E sang DTO D
     * @param <E>: kiểu dữ liệu entity
     * @param <D>: kiểu dữ liệu DTO
     * @return PageResponse<D>: kết quả phân trang dạng DTO
     */
    public <E, D> PageResponse<D> toPagedResponse(Page<E> entityPage, Function<E, D> converter) {

        // Lấy danh sách entity trong trang hiện tại rồi chuyển đổi từng entity -> DTO
        List<D> dtoList = entityPage.getContent().stream()
                .map(converter) // áp dụng hàm chuyển đổi E -> D
                .collect(Collectors.toList()); // thu thập thành List<D>

        // Tạo đối tượng PageResponse để chứa kết quả phân trang dạng DTO
        PageResponse<D> response = new PageResponse<>();

        response.setContent(dtoList); // danh sách DTO đã convert
        response.setPage(entityPage.getNumber()); // số trang hiện tại (0-based index)
        response.setSize(entityPage.getSize()); // số phần tử tối đa mỗi trang
        response.setTotalElements(entityPage.getTotalElements()); // tổng số phần tử trong toàn bộ dataset
        response.setTotalPages(entityPage.getTotalPages()); // tổng số trang
        response.setLast(entityPage.isLast()); // có phải trang cuối không?

        return response; // trả về kết quả cuối cùng
    }
}
