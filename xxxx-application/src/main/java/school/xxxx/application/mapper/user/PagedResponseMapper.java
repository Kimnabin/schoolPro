package school.xxxx.application.mapper.user;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import school.xxxx.application.model.dto.user.response.PageResponse;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PagedResponseMapper {

    public <E, D> PageResponse<D> toPagedResponse(Page<E> entityPage, Function<E, D> converter) {
        List<D> dtoList = entityPage.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());

        PageResponse<D> response = new PageResponse<>();
        response.setContent(dtoList);
        response.setPage(entityPage.getNumber());
        response.setSize(entityPage.getSize());
        response.setTotalElements(entityPage.getTotalElements());
        response.setTotalPages(entityPage.getTotalPages());
        response.setLast(entityPage.isLast());

        return response;
    }
}
