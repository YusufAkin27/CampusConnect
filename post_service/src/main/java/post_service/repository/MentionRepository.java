package post_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import post_service.entity.Mention;

import java.util.List;

@Repository
public interface MentionRepository extends JpaRepository<Mention, Long> {

    List<Mention> findByPostId(Long postId);

    void deleteByPostId(Long postId);
}
