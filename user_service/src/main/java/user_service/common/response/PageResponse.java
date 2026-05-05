package user_service.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated response wrapper for list-based endpoints.
 *
 * @param <T> Type of the items in the page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean last;

    /**
     * Creates a PageResponse from a Spring Data Page object and a mapped content list.
     *
     * @param page    Spring Data Page
     * @param content Mapped content list (already converted to DTO)
     * @param <T>     DTO type
     * @return PageResponse instance
     */
    public static <T> PageResponse<T> from(Page<?> page, List<T> content) {
        return PageResponse.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
