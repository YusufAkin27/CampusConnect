package post_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import post_service.entity.PostHashtag;

import java.util.List;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    List<PostHashtag> findByPostId(Long postId);

    void deleteByPostId(Long postId);

    @Query("SELECT ph FROM PostHashtag ph WHERE ph.hashtag.name = :hashtagName")
    Page<PostHashtag> findByHashtagName(@Param("hashtagName") String hashtagName, Pageable pageable);
}
