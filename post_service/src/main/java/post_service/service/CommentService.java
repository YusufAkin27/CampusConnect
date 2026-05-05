package post_service.service;

import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.common.response.ResponseMessage;
import post_service.dto.request.CreateCommentRequest;
import post_service.dto.request.UpdateCommentRequest;
import post_service.dto.response.CommentResponse;

public interface CommentService {

    DataResponseMessage<CommentResponse> createComment(Long authUserId, Long postId, CreateCommentRequest request);

    DataResponseMessage<CommentResponse> updateComment(Long authUserId, Long commentId, UpdateCommentRequest request);

    ResponseMessage deleteComment(Long authUserId, Long commentId);

    DataResponseMessage<PageResponse<CommentResponse>> getPostComments(Long authUserId, Long postId, int page, int size);

    DataResponseMessage<PageResponse<CommentResponse>> getCommentReplies(Long authUserId, Long commentId, int page, int size);
}
