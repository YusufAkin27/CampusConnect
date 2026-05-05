package user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * CampusConnect - User Service
 *
 * <p>This microservice is responsible for user profile management.
 * It handles profile creation, updates, search, and internal profile queries.
 * Authentication and token management are handled by auth-service.
 * </p>
 *
 * <p>Port: 8082 | Consul: user-service | DB: campusconnect_users</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
