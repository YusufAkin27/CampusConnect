package post_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import post_service.entity.SavedPost;

import java.util.Optional;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    Optional<SavedPost> findByPostIdAndAuthUserId(Long postId, Long authUserId);

    boolean existsByPostIdAndAuthUserId(Long postId, Long authUserId);

    void deleteByPostIdAndAuthUserId(Long postId, Long authUserId);

    Page<SavedPost> findByAuthUserId(Long authUserId, Pageable pageable);
}
