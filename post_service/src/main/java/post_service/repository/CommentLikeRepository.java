package post_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import post_service.entity.CommentLike;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentIdAndAuthUserId(Long commentId, Long authUserId);

    boolean existsByCommentIdAndAuthUserId(Long commentId, Long authUserId);

    void deleteByCommentIdAndAuthUserId(Long commentId, Long authUserId);
}
