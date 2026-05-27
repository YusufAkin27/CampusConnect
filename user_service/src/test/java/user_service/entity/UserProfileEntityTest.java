package user_service.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import user_service.enums.AccountStatus;
import user_service.enums.ProfileVisibility;
import user_service.repository.UserProfileRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
})
class UserProfileEntityTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void prePersist_ShouldSetDefaultsAndTimestamps() {
        UserProfile profile = UserProfile.builder()
                .authUserId(10L)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .build();

        UserProfile saved = userProfileRepository.save(profile);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getProfileVisibility()).isEqualTo(ProfileVisibility.PUBLIC);
        assertThat(saved.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(saved.getProfileCompleted()).isFalse();
    }
}
