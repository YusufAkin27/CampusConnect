package post_service.service;

import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.common.response.ResponseMessage;
import post_service.dto.response.PostResponse;

public interface SavedPostService {

    ResponseMessage savePost(Long authUserId, Long postId);

    ResponseMessage unsavePost(Long authUserId, Long postId);

    DataResponseMessage<PageResponse<PostResponse>> getSavedPosts(Long authUserId, int page, int size);
}
