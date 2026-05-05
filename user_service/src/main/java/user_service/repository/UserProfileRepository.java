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

    Optional<UserProfile> findByEmail(String email);

    boolean existsByAuthUserId(Long authUserId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByStudentNumber(String studentNumber);

    Page<UserProfile> findByAccountStatus(AccountStatus status, Pageable pageable);

    /**
     * Searches active users by keyword across multiple text fields.
     * All filter parameters (faculty, department, grade) are optional.
     *
     * @param keyword    search term matched against username, firstName, lastName, displayName, email
     * @param faculty    optional faculty filter
     * @param department optional department filter
     * @param grade      optional grade filter
     * @param pageable   pagination info
     * @return page of matching UserProfile entities
     */
    @Query("""
            SELECT u FROM UserProfile u
            WHERE u.accountStatus = 'ACTIVE'
            AND (
                :keyword IS NULL OR :keyword = ''
                OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:faculty IS NULL OR u.faculty = :faculty)
            AND (:department IS NULL OR u.department = :department)
            AND (:grade IS NULL OR u.grade = :grade)
            ORDER BY u.firstName ASC, u.lastName ASC
            """)
    Page<UserProfile> searchUsers(
            @Param("keyword") String keyword,
            @Param("faculty") Faculty faculty,
            @Param("department") Department department,
            @Param("grade") Grade grade,
            Pageable pageable
    );

    /**
     * Checks if a student number is used by someone other than the given user (for update uniqueness check).
     */
    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
            FROM UserProfile u
            WHERE u.studentNumber = :studentNumber AND u.id <> :excludeId
            """)
    boolean existsByStudentNumberAndIdNot(
            @Param("studentNumber") String studentNumber,
            @Param("excludeId") Long excludeId
    );
}
