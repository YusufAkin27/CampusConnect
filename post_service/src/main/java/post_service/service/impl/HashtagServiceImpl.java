package post_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import post_service.client.UserServiceClient;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.dto.response.HashtagResponse;
import post_service.dto.response.PostResponse;
import post_service.dto.response.UserSummaryResponse;
import post_service.entity.Hashtag;
import post_service.entity.Post;
import post_service.exception.HashtagNotFoundException;
import post_service.mapper.HashtagMapper;
import post_service.mapper.PostMapper;
import post_service.repository.*;
import post_service.service.HashtagService;
import post_service.util.PageResponseConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PostLikeRepository postLikeRepository;
    private final SavedPostRepository savedPostRepository;
    private final UserServiceClient userServiceClient;
    private final HashtagMapper hashtagMapper;
    private final PostMapper postMapper;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<HashtagResponse>> searchHashtags(String keyword, int page, int size) {
        Page<Hashtag> hashtags = hashtagRepository.findByNameContainingIgnoreCase(
                keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "usageCount")));
        Page<HashtagResponse> mapped = hashtags.map(hashtagMapper::toHashtagResponse);
        return DataResponseMessage.success("Hashtags found.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<List<HashtagResponse>> getTrendingHashtags() {
        List<HashtagResponse> trending = hashtagRepository.findTop10ByOrderByUsageCountDesc()
                .stream().map(hashtagMapper::toHashtagResponse).collect(Collectors.toList());
        return DataResponseMessage.success("Trending hashtags retrieved.", trending);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<PostResponse>> getPostsByHashtag(Long authUserId, String hashtag, int page, int size) {
        String normalizedHashtag = hashtag.startsWith("#") ? hashtag.substring(1).toLowerCase() : hashtag.toLowerCase();

        hashtagRepository.findByName(normalizedHashtag)
                .orElseThrow(() -> new HashtagNotFoundException(normalizedHashtag));

        Page<post_service.entity.PostHashtag> postHashtags = postHashtagRepository.findByHashtagName(
                normalizedHashtag, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        Page<PostResponse> mapped = postHashtags.map(ph -> {
            Post p = ph.getPost();
            UserSummaryResponse author = userServiceClient.getUserByAuthUserId(p.getAuthUserId());
            PostResponse response = postMapper.toPostResponse(p, author, authUserId);
            if (authUserId != null) {
                boolean likedByMe = postLikeRepository.existsByPostIdAndAuthUserId(p.getId(), authUserId);
                boolean savedByMe = savedPostRepository.existsByPostIdAndAuthUserId(p.getId(), authUserId);
                response.setLikedByMe(likedByMe);
                response.setSavedByMe(savedByMe);
                if (likedByMe) {
                    postLikeRepository.findByPostIdAndAuthUserId(p.getId(), authUserId)
                            .ifPresent(like -> response.setMyReaction(like.getReactionType()));
                }
            }
            return response;
        });

        return DataResponseMessage.success("Posts for hashtag retrieved.", pageResponseConverter.toPageResponse(mapped));
    }
}
