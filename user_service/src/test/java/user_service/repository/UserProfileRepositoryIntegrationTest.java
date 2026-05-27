package user_service.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import user_service.entity.UserProfile;
import user_service.enums.AccountStatus;
import user_service.enums.ProfileVisibility;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
})
class UserProfileRepositoryIntegrationTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void repositoryMethods_ShouldWorkForFindAndExists() {
        UserProfile profile = userProfileRepository.save(baseProfile("john_doe", 10L, "John"));

        assertThat(userProfileRepository.findByAuthUserId(10L)).isPresent();
        assertThat(userProfileRepository.findByUsername("john_doe")).isPresent();
        assertThat(userProfileRepository.existsByAuthUserId(10L)).isTrue();
        assertThat(userProfileRepository.existsByUsername("john_doe")).isTrue();
        assertThat(profile.getId()).isNotNull();
    }

    @Test
    void searchUsers_ShouldReturnOnlyActiveUsers() {
        userProfileRepository.save(baseProfile("active_user", 11L, "Active"));

        UserProfile inactive = baseProfile("inactive_user", 12L, "Inactive");
        inactive.setAccountStatus(AccountStatus.PASSIVE);
        userProfileRepository.save(inactive);

        List<UserProfile> results = userProfileRepository.searchUsers(null, org.springframework.data.domain.PageRequest.of(0, 10))
                .getContent();

        assertThat(results).extracting(UserProfile::getUsername)
                .contains("active_user")
                .doesNotContain("inactive_user");
    }

    @Test
    void searchUsers_ShouldFilterByKeyword() {
        userProfileRepository.save(baseProfile("john_doe", 10L, "John"));
        userProfileRepository.save(baseProfile("alice", 11L, "Alice"));

        List<UserProfile> results = userProfileRepository.searchUsers("john", org.springframework.data.domain.PageRequest.of(0, 10))
                .getContent();

        assertThat(results).extracting(UserProfile::getUsername)
                .containsExactly("john_doe");
    }

    private UserProfile baseProfile(String username, Long authUserId, String firstName) {
        return UserProfile.builder()
                .authUserId(authUserId)
                .username(username)
                .firstName(firstName)
                .lastName("Doe")
                .profileVisibility(ProfileVisibility.PUBLIC)
                .accountStatus(AccountStatus.ACTIVE)
                .profileCompleted(false)
                .build();
    }
}
