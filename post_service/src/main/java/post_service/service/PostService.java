package post_service.service;

import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.common.response.ResponseMessage;
import post_service.dto.request.CreatePostRequest;
import post_service.dto.request.UpdatePostRequest;
import post_service.dto.request.UpdatePostStatusRequest;
import post_service.dto.response.PostDetailResponse;
import post_service.dto.response.PostResponse;
import post_service.dto.response.PostStatsResponse;
import post_service.enums.SortType;

public interface PostService {

    DataResponseMessage<PostResponse> createPost(Long authUserId, CreatePostRequest request);

    DataResponseMessage<PostResponse> updatePost(Long authUserId, Long postId, UpdatePostRequest request);

    ResponseMessage deletePost(Long authUserId, Long postId);

    DataResponseMessage<PostDetailResponse> getPostDetail(Long authUserId, Long postId);

    DataResponseMessage<PageResponse<PostResponse>> getFeed(Long authUserId, int page, int size, SortType sortType);

    DataResponseMessage<PageResponse<PostResponse>> getMyPosts(Long authUserId, int page, int size);

    DataResponseMessage<PageResponse<PostResponse>> getUserPublicPosts(Long requesterAuthUserId, Long targetAuthUserId, int page, int size);

    DataResponseMessage<PageResponse<PostResponse>> searchPosts(Long authUserId, String keyword, int page, int size);

    DataResponseMessage<PostStatsResponse> getPostStats(Long postId);

    ResponseMessage archivePost(Long authUserId, Long postId);

    ResponseMessage pinPost(Long authUserId, Long postId);

    ResponseMessage unpinPost(Long authUserId, Long postId);

    DataResponseMessage<PostResponse> updatePostStatus(Long postId, UpdatePostStatusRequest request);
}
