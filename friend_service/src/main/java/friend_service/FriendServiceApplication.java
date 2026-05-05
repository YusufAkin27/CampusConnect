package friend_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Entry point for the CampusConnect Friend Service.
 *
 * Responsibilities:
 * - Friendship requests (send, accept, reject, cancel)
 * - Friendship management (list, remove)
 * - Follow/unfollow system (follow, unfollow, followers, following)
 * - Social graph operations (mutual friends, suggested users)
 * - Internal endpoints for inter-service communication
 *
 * NOTE: Block/unblock operations are handled by block-service (not this service).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FriendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendServiceApplication.class, args);
    }
}
