package user_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import user_service.entity.UserProfile;
import user_service.enums.AccountStatus;
import user_service.enums.Department;
import user_service.enums.Faculty;
import user_service.enums.Grade;

import java.util.Optional;

/**
 * Repository for UserProfile entity.
 * Provides standard CRUD + custom query methods for user profile operations.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByAuthUserId(Long authUserId);

    Optional<UserProfile> findByUsername(String username);


    boolean existsByAuthUserId(Long authUserId);

    boolean existsByUsername(String username);




    @Query("""
            SELECT u FROM UserProfile u
            WHERE u.accountStatus = 'ACTIVE'
            AND (
                :keyword IS NULL OR :keyword = ''
                OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 )
             ORDER BY u.firstName ASC, u.lastName ASC
            """)
    Page<UserProfile> searchUsers(
            @Param("keyword") String keyword,
            Pageable pageable
    );


}
