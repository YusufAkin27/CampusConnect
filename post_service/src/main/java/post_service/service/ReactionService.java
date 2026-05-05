package post_service.service;

import post_service.common.response.DataResponseMessage;
import post_service.common.response.ResponseMessage;
import post_service.dto.request.ReactCommentRequest;
import post_service.dto.request.ReactPostRequest;
import post_service.dto.response.CommentResponse;
import post_service.dto.response.PostResponse;

public interface ReactionService {

    DataResponseMessage<PostResponse> reactToPost(Long authUserId, Long postId, ReactPostRequest request);

    ResponseMessage removePostReaction(Long authUserId, Long postId);

    DataResponseMessage<CommentResponse> reactToComment(Long authUserId, Long commentId, ReactCommentRequest request);

    ResponseMessage removeCommentReaction(Long authUserId, Long commentId);
}
