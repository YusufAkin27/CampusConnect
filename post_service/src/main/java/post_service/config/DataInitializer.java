package post_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import post_service.entity.Post;
import post_service.enums.PostType;
import post_service.enums.PostVisibility;
import post_service.repository.PostRepository;
import post_service.enums.PostStatus;

/**
 * Seeds sample data on application start if app.seed.enabled=true.
 * Only for development/testing purposes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PostRepository postRepository;

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Override
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            log.info("Data seeding is disabled. Set app.seed.enabled=true to enable.");
            return;
        }

        long count = postRepository.count();
        if (count > 0) {
            log.info("Database already has {} posts. Skipping seed.", count);
            return;
        }

        log.info("Seeding sample posts...");

        postRepository.save(Post.builder()
                .authUserId(1L)
                .content("Hello Campus! #campusconnect #welcome @everyone")
                .postType(PostType.TEXT)
                .visibility(PostVisibility.PUBLIC)
                .commentsEnabled(true)
                .likesEnabled(true)
                .pinned(false)
                .build());

        postRepository.save(Post.builder()
                .authUserId(1L)
                .content("Check out this amazing campus photo! #campus #photo")
                .postType(PostType.IMAGE)
                .visibility(PostVisibility.PUBLIC)
                .commentsEnabled(true)
                .likesEnabled(true)
                .pinned(false)
                .build());

        postRepository.save(Post.builder()
                .authUserId(2L)
                .content("Important announcement for all students. #announcement")
                .postType(PostType.ANNOUNCEMENT)
                .visibility(PostVisibility.UNIVERSITY_ONLY)
                .commentsEnabled(true)
                .likesEnabled(true)
                .pinned(true)
                .build());

        log.info("Sample posts seeded successfully.");
    }
}
