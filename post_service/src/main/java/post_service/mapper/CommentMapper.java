package post_service.mapper;

import org.springframework.stereotype.Component;
import post_service.dto.response.CommentResponse;
import post_service.dto.response.CommentSummaryResponse;
import post_service.dto.response.UserSummaryResponse;
import post_service.entity.Comment;

import java.util.List;

/**
 * Manual mapper for Comment entity to response DTOs.
 */
@Component
public class CommentMapper {

    public CommentResponse toCommentResponse(Comment comment, UserSummaryResponse author, Long currentAuthUserId) {
        if (comment == null) return null;

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .authUserId(comment.getAuthUserId())
                .author(author)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .content(comment.getContent())
                .status(comment.getStatus())
                .likeCount(comment.getLikeCount())
                .replyCount(comment.getReplyCount())
                .reportCount(comment.getReportCount())
                .likedByMe(false)   // set by service
                .myReaction(null)   // set by service
                .edited(comment.getEdited())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(List.of())  // set by service on demand
                .build();
    }

    public CommentSummaryResponse toCommentSummaryResponse(Comment comment) {
        if (comment == null) return null;
        return CommentSummaryResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .authUserId(comment.getAuthUserId())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .replyCount(comment.getReplyCount())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
