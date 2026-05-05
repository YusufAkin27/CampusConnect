package post_service.service;

import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.dto.response.HashtagResponse;
import post_service.dto.response.PostResponse;

import java.util.List;

public interface HashtagService {

    DataResponseMessage<PageResponse<HashtagResponse>> searchHashtags(String keyword, int page, int size);

    DataResponseMessage<List<HashtagResponse>> getTrendingHashtags();

    DataResponseMessage<PageResponse<PostResponse>> getPostsByHashtag(Long authUserId, String hashtag, int page, int size);
}
