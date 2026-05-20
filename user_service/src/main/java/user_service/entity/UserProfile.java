package user_service.entity;

import jakarta.persistence.*;
import lombok.*;
import user_service.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * UserProfile entity - stores all profile-related data for a user.
 * Authentication data (password, token, login info) is managed by auth-service.
 * This entity references auth-service users via authUserId.
 */
@Entity
@Table(
        name = "user_profiles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_profile_auth_user_id", columnNames = "auth_user_id"),
                @UniqueConstraint(name = "uq_user_profile_username", columnNames = "username"),
                @UniqueConstraint(name = "uq_user_profile_email", columnNames = "email"),
                @UniqueConstraint(name = "uq_user_profile_student_number", columnNames = "student_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * References the user id in auth-service.
     * Used for internal service communication.
     */
    @Column(name = "auth_user_id", nullable = false, unique = true)
    private Long authUserId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", length = 20, nullable = false)
    @Builder.Default
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", length = 20, nullable = false)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "profile_completed", nullable = false)
    @Builder.Default
    private Boolean profileCompleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Sets createdAt and updatedAt before persisting for the first time.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.profileVisibility == null) {
            this.profileVisibility = ProfileVisibility.PUBLIC;
        }
        if (this.accountStatus == null) {
            this.accountStatus = AccountStatus.ACTIVE;
        }
        if (this.profileCompleted == null) {
            this.profileCompleted = false;
        }
    }

    /**
     * Updates updatedAt before every update operation.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
