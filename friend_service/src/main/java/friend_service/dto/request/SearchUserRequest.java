package friend_service.dto.request;

import lombok.Data;

/**
 * Request model for searching users with relationship context.
 * Parameters are passed as query params, not request body.
 *
 * NOTE: The actual user search is delegated to user-service.
 * Friend-service enriches the results with social relation information.
 */
@Data
public class SearchUserRequest {

    private String keyword;
    private String faculty;
    private String department;
    private String grade;
    private int page = 0;
    private int size = 20;
}
