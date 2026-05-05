package friend_service.util;

import friend_service.dto.response.UserSummaryResponse;
import friend_service.enums.SuggestionReason;
import org.springframework.stereotype.Component;

/**
 * Calculates a relevance score and primary suggestion reason for a candidate user
 * relative to the authenticated user's profile attributes.
 *
 * Scoring weights:
 * - Same department : 40 points (highest relevance)
 * - Same faculty    : 20 points
 * - Same grade      : 10 points
 * - Mutual friends  :  5 points per mutual friend (capped at 25)
 */
@Component
public class SuggestionScoreCalculator {

    private static final int SAME_DEPARTMENT_SCORE = 40;
    private static final int SAME_FACULTY_SCORE = 20;
    private static final int SAME_GRADE_SCORE = 10;
    private static final int PER_MUTUAL_FRIEND_SCORE = 5;
    private static final int MUTUAL_FRIEND_CAP = 25;

    /**
     * Calculates the suggestion score for a candidate user.
     *
     * @param currentUser   the authenticated user's summary
     * @param candidate     the candidate user being evaluated
     * @param mutualCount   the number of mutual friends
     * @return a numeric score (higher = more relevant)
     */
    public int calculateScore(
            UserSummaryResponse currentUser,
            UserSummaryResponse candidate,
            long mutualCount
    ) {
        int score = 0;

        if (isSameDepartment(currentUser, candidate)) {
            score += SAME_DEPARTMENT_SCORE;
        } else if (isSameFaculty(currentUser, candidate)) {
            score += SAME_FACULTY_SCORE;
        }

        if (isSameGrade(currentUser, candidate)) {
            score += SAME_GRADE_SCORE;
        }

        score += (int) Math.min(mutualCount * PER_MUTUAL_FRIEND_SCORE, MUTUAL_FRIEND_CAP);

        return score;
    }

    /**
     * Determines the primary reason to suggest this user.
     *
     * @param currentUser  the authenticated user's summary
     * @param candidate    the candidate user being evaluated
     * @param mutualCount  the number of mutual friends
     * @return the most relevant SuggestionReason
     */
    public SuggestionReason determinePrimaryReason(
            UserSummaryResponse currentUser,
            UserSummaryResponse candidate,
            long mutualCount
    ) {
        if (mutualCount > 0) {
            return SuggestionReason.MUTUAL_FRIENDS;
        }
        if (isSameDepartment(currentUser, candidate)) {
            return SuggestionReason.SAME_DEPARTMENT;
        }
        if (isSameFaculty(currentUser, candidate)) {
            return SuggestionReason.SAME_FACULTY;
        }
        if (isSameGrade(currentUser, candidate)) {
            return SuggestionReason.SAME_GRADE;
        }
        return SuggestionReason.POPULAR_IN_UNIVERSITY;
    }

    public boolean isSameFaculty(UserSummaryResponse a, UserSummaryResponse b) {
        return a.getFaculty() != null && a.getFaculty().equalsIgnoreCase(b.getFaculty());
    }

    public boolean isSameDepartment(UserSummaryResponse a, UserSummaryResponse b) {
        return a.getDepartment() != null && a.getDepartment().equalsIgnoreCase(b.getDepartment());
    }

    public boolean isSameGrade(UserSummaryResponse a, UserSummaryResponse b) {
        return a.getGrade() != null && a.getGrade().equalsIgnoreCase(b.getGrade());
    }
}
