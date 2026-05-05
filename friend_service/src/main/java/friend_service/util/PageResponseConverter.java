package friend_service.util;

import friend_service.common.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Converts Spring Data {@link Page} objects into {@link PageResponse} wrappers.
 */
@Component
public class PageResponseConverter {

    /**
     * Converts a Spring Data Page into a PageResponse, preserving all pagination metadata.
     *
     * @param page the Spring Data page result
     * @param <T>  the element type
     * @return the wrapped PageResponse
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
