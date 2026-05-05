package post_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import post_service.entity.Comment;
import post_service.enums.CommentStatus;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostIdAndParentCommentIsNullAndStatus(
            Long postId,
            CommentStatus status,
            Pageable pageable
    );

    Page<Comment> findByParentCommentIdAndStatus(
            Long parentCommentId,
            CommentStatus status,
            Pageable pageable
    );

    Optional<Comment> findByIdAndStatusNot(Long id, CommentStatus status);

    Long countByPostIdAndStatus(Long postId, CommentStatus status);
}
