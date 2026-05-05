package post_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import post_service.entity.Hashtag;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);

    Page<Hashtag> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    List<Hashtag> findTop10ByOrderByUsageCountDesc();
}
