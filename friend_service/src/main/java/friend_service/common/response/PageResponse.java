package friend_service.common.response;

import lombok.*;

import java.util.List;

/**
 * Paginated response wrapper for list endpoints.
 * Wraps Spring Data Page metadata alongside the content.
 *
 * @param <T> the type of items in the page content list
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
}
