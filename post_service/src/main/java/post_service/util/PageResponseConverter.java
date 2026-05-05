package post_service.util;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import post_service.common.response.PageResponse;

/**
 * Converts Spring Data Page objects into PageResponse wrappers.
 */
@Component
public class PageResponseConverter {

    /**
     * Converts a Page of type T into a PageResponse of type T.
     */
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
