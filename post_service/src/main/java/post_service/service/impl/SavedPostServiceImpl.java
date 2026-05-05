package post_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import post_service.client.UserServiceClient;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.common.response.ResponseMessage;
import post_service.dto.response.PostResponse;
import post_service.dto.response.UserSummaryResponse;
import post_service.entity.Post;
import post_service.entity.SavedPost;
import post_service.enums.PostStatus;
import post_service.exception.*;
import post_service.mapper.PostMapper;
import post_service.repository.PostLikeRepository;
import post_service.repository.PostRepository;
import post_service.repository.SavedPostRepository;
import post_service.service.SavedPostService;
import post_service.util.PageResponseConverter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedPostServiceImpl implements SavedPostService {

    private final SavedPostRepository savedPostRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional
    public ResponseMessage savePost(Long authUserId, Long postId) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new InactivePostException(postId);
        }

        if (savedPostRepository.existsByPostIdAndAuthUserId(postId, authUserId)) {
            return ResponseMessage.success("Post is already saved.");
        }

        savedPostRepository.save(SavedPost.builder()
                .post(post)
                .authUserId(authUserId)
                .build());

        post.incrementSaveCount();
        postRepository.save(post);

        return ResponseMessage.success("Post saved successfully.");
    }

    @Override
    @Transactional
    public ResponseMessage unsavePost(Long authUserId, Long postId) {
        postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!savedPostRepository.existsByPostIdAndAuthUserId(postId, authUserId)) {
            return ResponseMessage.success("Post was not saved.");
        }

        savedPostRepository.deleteByPostIdAndAuthUserId(postId, authUserId);

        postRepository.findById(postId).ifPresent(post -> {
            post.decrementSaveCount();
            postRepository.save(post);
        });

        return ResponseMessage.success("Post unsaved successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<PostResponse>> getSavedPosts(Long authUserId, int page, int size) {
        Page<SavedPost> savedPosts = savedPostRepository.findByAuthUserId(
                authUserId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        Page<PostResponse> mapped = savedPosts.map(savedPost -> {
            Post p = savedPost.getPost();
            UserSummaryResponse author = userServiceClient.getUserByAuthUserId(p.getAuthUserId());
            PostResponse response = postMapper.toPostResponse(p, author, authUserId);
            boolean likedByMe = postLikeRepository.existsByPostIdAndAuthUserId(p.getId(), authUserId);
            response.setLikedByMe(likedByMe);
            response.setSavedByMe(true);
            if (likedByMe) {
                postLikeRepository.findByPostIdAndAuthUserId(p.getId(), authUserId)
                        .ifPresent(like -> response.setMyReaction(like.getReactionType()));
            }
            return response;
        });

        return DataResponseMessage.success("Saved posts retrieved successfully.", pageResponseConverter.toPageResponse(mapped));
    }
}
