package post_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import post_service.entity.PostLike;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndAuthUserId(Long postId, Long authUserId);

    boolean existsByPostIdAndAuthUserId(Long postId, Long authUserId);

    Long countByPostId(Long postId);

    void deleteByPostIdAndAuthUserId(Long postId, Long authUserId);
}
