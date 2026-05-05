package post_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import post_service.entity.Post;
import post_service.enums.PostStatus;
import post_service.enums.PostVisibility;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndStatusNot(Long id, PostStatus status);

    Page<Post> findByAuthUserIdAndStatus(Long authUserId, PostStatus status, Pageable pageable);

    Page<Post> findByStatusAndVisibility(PostStatus status, PostVisibility visibility, Pageable pageable);

    Page<Post> findByAuthUserIdAndStatusAndVisibility(Long authUserId, PostStatus status, PostVisibility visibility, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = post_service.enums.PostStatus.ACTIVE " +
           "AND p.visibility = post_service.enums.PostVisibility.PUBLIC " +
           "AND LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = post_service.enums.PostStatus.ACTIVE " +
           "AND (p.visibility = post_service.enums.PostVisibility.PUBLIC " +
           "OR p.visibility = post_service.enums.PostVisibility.UNIVERSITY_ONLY) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findFeedPosts(Pageable pageable);

    Long countByAuthUserIdAndStatus(Long authUserId, PostStatus status);
}
