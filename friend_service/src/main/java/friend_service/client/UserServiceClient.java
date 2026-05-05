package friend_service.client;

import friend_service.common.response.PageResponse;
import friend_service.dto.response.UserSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.Collections;

/**
 * HTTP client for communicating with user-service via Consul service discovery.
 *
 * Endpoints consumed:
 * - GET /v1/api/users/internal/auth/{authUserId}
 * - GET /v1/api/users/search?keyword=&faculty=&department=&grade=&page=&size=
 *
 * Fallback behavior:
 * - If user-service is unavailable, getUserByAuthUserId returns an "Unknown User" fallback.
 * - If user-service is unavailable, searchUsers returns an empty page.
 */
@Slf4j
@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(@Qualifier("userServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Fetches a user's profile summary from user-service by their authUserId.
     *
     * @param authUserId the auth-service assigned user ID
     * @return UserSummaryResponse or fallback if user-service is unavailable
     */
    public UserSummaryResponse getUserByAuthUserId(Long authUserId) {
        try {
            UserSummaryResponse response = webClient.get()
                    .uri("/v1/api/users/internal/auth/{authUserId}", authUserId)
                    .retrieve()
                    .bodyToMono(UserSummaryResponse.class)
                    .block();

            if (response == null) {
                log.warn("user-service returned null for authUserId={}, using fallback", authUserId);
                return UserSummaryResponse.fallback(authUserId);
            }
            return response;

        } catch (WebClientException e) {
            log.error("Failed to fetch user from user-service for authUserId={}: {}", authUserId, e.getMessage());
            return UserSummaryResponse.fallback(authUserId);
        } catch (Exception e) {
            log.error("Unexpected error fetching user authUserId={}: {}", authUserId, e.getMessage());
            return UserSummaryResponse.fallback(authUserId);
        }
    }

    /**
     * Searches users in user-service with optional filters.
     *
     * @param keyword    search keyword (username, displayName)
     * @param faculty    faculty filter (optional)
     * @param department department filter (optional)
     * @param grade      grade/year filter (optional)
     * @param page       page number (0-indexed)
     * @param size       page size
     * @return PageResponse of UserSummaryResponse, or empty page on failure
     */
    public PageResponse<UserSummaryResponse> searchUsers(
            String keyword,
            String faculty,
            String department,
            String grade,
            int page,
            int size
    ) {
        try {
            PageResponse<UserSummaryResponse> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/api/users/search")
                            .queryParamIfPresent("keyword", java.util.Optional.ofNullable(keyword))
                            .queryParamIfPresent("faculty", java.util.Optional.ofNullable(faculty))
                            .queryParamIfPresent("department", java.util.Optional.ofNullable(department))
                            .queryParamIfPresent("grade", java.util.Optional.ofNullable(grade))
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PageResponse<UserSummaryResponse>>() {})
                    .block();

            if (response == null) {
                log.warn("user-service returned null for searchUsers, returning empty page");
                return emptyPage(page, size);
            }
            return response;

        } catch (WebClientException e) {
            log.error("Failed to search users from user-service: {}", e.getMessage());
            return emptyPage(page, size);
        } catch (Exception e) {
            log.error("Unexpected error during user search: {}", e.getMessage());
            return emptyPage(page, size);
        }
    }

    private PageResponse<UserSummaryResponse> emptyPage(int page, int size) {
        return PageResponse.<UserSummaryResponse>builder()
                .content(Collections.emptyList())
                .page(page)
                .size(size)
                .totalElements(0)
                .totalPages(0)
                .last(true)
                .build();
    }
}
