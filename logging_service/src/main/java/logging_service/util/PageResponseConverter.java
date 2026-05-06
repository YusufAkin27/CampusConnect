package logging_service.util;

import logging_service.common.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility to convert Spring Data Page objects into PageResponse wrappers.
 */
@Component
public class PageResponseConverter {

    /**
     * Converts a Spring Data Page<S> to PageResponse<T> using a mapper function.
     *
     * @param page   the Spring Data page
     * @param mapper function to convert each element
     * @param <S>    source type
     * @param <T>    target type
     * @return PageResponse containing mapped content and pagination metadata
     */
    public <S, T> PageResponse<T> convert(Page<S> page, Function<S, T> mapper) {
        List<T> content = page.getContent()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PageResponse.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    /**
     * Converts a Spring Data Page<T> to PageResponse<T> without mapping.
     */
    public <T> PageResponse<T> convert(Page<T> page) {
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
