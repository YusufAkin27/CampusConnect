package media_service.util;

import media_service.common.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Converts Spring Data Page<T> objects to our custom PageResponse<T>.
 */
@Component
public class PageResponseConverter {

    public <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
