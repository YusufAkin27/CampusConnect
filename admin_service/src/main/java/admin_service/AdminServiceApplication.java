package admin_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CampusConnect - Admin Service
 *
 * <p>Central administration microservice for managing users, posts, media,
 * reports, support tickets, notifications, system health and audit logs.
 * Communicates with other services via Feign Clients.</p>
 *
 * <p>Port: 8088 | Consul: admin-service | DB: campusconnect_admin</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class AdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
