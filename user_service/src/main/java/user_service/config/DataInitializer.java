package user_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import user_service.entity.UserProfile;
import user_service.enums.*;
import user_service.repository.UserProfileRepository;

/**
 * Development data initializer.
 * Creates sample user profiles on startup when {@code app.seed.enabled=true}.
 *
 * <p><b>WARNING:</b> Never enable in production. Set {@code app.seed.enabled=false} in production properties.</p>
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserProfileRepository userProfileRepository;

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.debug("Data seeding is disabled. Set app.seed.enabled=true to enable.");
            return;
        }

        log.info("Data seeding is enabled. Checking and creating sample profiles...");
        seedSampleProfiles();
        log.info("Data seeding completed.");
    }

    private void seedSampleProfiles() {
        createIfNotExists(
                1001L,
                "ahmet.yilmaz",
                "ahmet.yilmaz@university.edu.tr",
                "Ahmet",
                "Yılmaz",
                Faculty.ENGINEERING,
                Department.COMPUTER_ENGINEERING,
                Grade.THIRD_GRADE,
                "20210001001"
        );

        createIfNotExists(
                1002L,
                "zeynep.kaya",
                "zeynep.kaya@university.edu.tr",
                "Zeynep",
                "Kaya",
                Faculty.ENGINEERING,
                Department.SOFTWARE_ENGINEERING,
                Grade.PREPARATION,
                "20230001002"
        );

        createIfNotExists(
                1003L,
                "mehmet.demir",
                "mehmet.demir@university.edu.tr",
                "Mehmet",
                "Demir",
                Faculty.SCIENCE,
                Department.MATHEMATICS,
                Grade.GRADUATED,
                null
        );
    }

    private void createIfNotExists(
            Long authUserId,
            String username,
            String email,
            String firstName,
            String lastName,
            Faculty faculty,
            Department department,
            Grade grade,
            String studentNumber) {

        if (userProfileRepository.existsByAuthUserId(authUserId)) {
            log.debug("Sample profile already exists for authUserId: {}. Skipping.", authUserId);
            return;
        }

        UserProfile profile = UserProfile.builder()
                .authUserId(authUserId)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .displayName(firstName + " " + lastName)
                .faculty(faculty)
                .department(department)
                .grade(grade)
                .studentNumber(studentNumber)
                .profileVisibility(ProfileVisibility.PUBLIC)
                .accountStatus(AccountStatus.ACTIVE)
                .profileCompleted(false)
                .bio("Sample profile for development environment.")
                .build();

        userProfileRepository.save(profile);
        log.info("Created sample profile: {} (authUserId: {})", username, authUserId);
    }
}
