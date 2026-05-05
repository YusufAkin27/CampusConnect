package post_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import post_service.client.dto.InternalUserResponse;
import post_service.dto.response.UserSummaryResponse;

/**
 * Client for calling user-service internal endpoints.
 * Uses WebClient with Consul-based load balancing (lb://user-service).
 * Falls back to a placeholder response if user-service is unavailable.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${user-service.base-url:http://user-service}")
    private String userServiceBaseUrl;

    /**
     * Retrieves a UserSummaryResponse by authUserId from user-service.
     * Returns a fallback response if user-service is unavailable.
     */
    public UserSummaryResponse getUserByAuthUserId(Long authUserId) {
        try {
            InternalUserResponse response = webClientBuilder.build()
                    .get()
                    .uri(userServiceBaseUrl + "/v1/api/users/internal/auth/{authUserId}", authUserId)
                    .retrieve()
                    .bodyToMono(InternalUserResponse.class)
                    .block();

            if (response == null) {
                log.warn("user-service returned null for authUserId={}", authUserId);
                return UserSummaryResponse.fallback(authUserId);
            }

            return mapToUserSummary(response);
        } catch (WebClientResponseException e) {
            log.warn("user-service returned HTTP {} for authUserId={}: {}", e.getStatusCode(), authUserId, e.getMessage());
            return UserSummaryResponse.fallback(authUserId);
        } catch (Exception e) {
            log.warn("user-service unavailable for authUserId={}: {}", authUserId, e.getMessage());
            return UserSummaryResponse.fallback(authUserId);
        }
    }

    /**
     * Retrieves a UserSummaryResponse by username from user-service.
     * Returns a fallback response if user-service is unavailable.
     */
    public UserSummaryResponse getUserByUsername(String username) {
        try {
            InternalUserResponse response = webClientBuilder.build()
                    .get()
                    .uri(userServiceBaseUrl + "/v1/api/users/internal/username/{username}", username)
                    .retrieve()
                    .bodyToMono(InternalUserResponse.class)
                    .block();

            if (response == null) {
                log.warn("user-service returned null for username={}", username);
                return buildFallbackByUsername(username);
            }

            return mapToUserSummary(response);
        } catch (WebClientResponseException e) {
            log.warn("user-service returned HTTP {} for username={}: {}", e.getStatusCode(), username, e.getMessage());
            return buildFallbackByUsername(username);
        } catch (Exception e) {
            log.warn("user-service unavailable for username={}: {}", username, e.getMessage());
            return buildFallbackByUsername(username);
        }
    }

    private UserSummaryResponse mapToUserSummary(InternalUserResponse r) {
        return UserSummaryResponse.builder()
                .id(r.getId())
                .authUserId(r.getAuthUserId())
                .username(r.getUsername())
                .displayName(r.getDisplayName())
                .profileImageUrl(r.getProfileImageUrl())
                .faculty(r.getFaculty())
                .department(r.getDepartment())
                .grade(r.getGrade())
                .build();
    }

    private UserSummaryResponse buildFallbackByUsername(String username) {
        return UserSummaryResponse.builder()
                .username(username)
                .displayName("Unknown User")
                .profileImageUrl(null)
                .build();
    }
}
