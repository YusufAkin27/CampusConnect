package post_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import post_service.client.UserServiceClient;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.common.response.ResponseMessage;
import post_service.dto.request.CreatePostRequest;
import post_service.dto.request.UpdatePostRequest;
import post_service.dto.request.UpdatePostStatusRequest;
import post_service.dto.response.*;
import post_service.entity.*;
import post_service.enums.*;
import post_service.exception.*;
import post_service.mapper.CommentMapper;
import post_service.mapper.PostMapper;
import post_service.repository.*;
import post_service.service.PostService;
import post_service.util.HashtagExtractor;
import post_service.util.MentionExtractor;
import post_service.util.PageResponseConverter;
import post_service.util.PostTypeResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final MentionRepository mentionRepository;
    private final HashtagRepository hashtagRepository;
    private final PostLikeRepository postLikeRepository;
    private final SavedPostRepository savedPostRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final HashtagExtractor hashtagExtractor;
    private final MentionExtractor mentionExtractor;
    private final PostTypeResolver postTypeResolver;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional
    public DataResponseMessage<PostResponse> createPost(Long authUserId, CreatePostRequest request) {
        // Validate: content and mediaList cannot both be empty
        boolean hasContent = request.getContent() != null && !request.getContent().isBlank();
        boolean hasMedia = request.getMediaList() != null && !request.getMediaList().isEmpty();
        if (!hasContent && !hasMedia) {
            throw new InvalidPostDataException("Post must have either content or at least one media item.");
        }
        if (request.getMediaList() != null && request.getMediaList().size() > 10) {
            throw new InvalidPostDataException("Post cannot have more than 10 media items.");
        }

        PostType postType = postTypeResolver.resolvePostType(request.getContent(), request.getMediaList());
        PostVisibility visibility = request.getVisibility() != null ? request.getVisibility() : PostVisibility.PUBLIC;
        Boolean commentsEnabled = request.getCommentsEnabled() != null ? request.getCommentsEnabled() : true;
        Boolean likesEnabled = request.getLikesEnabled() != null ? request.getLikesEnabled() : true;

        Post post = Post.builder()
                .authUserId(authUserId)
                .content(request.getContent())
                .postType(postType)
                .visibility(visibility)
                .commentsEnabled(commentsEnabled)
                .likesEnabled(likesEnabled)
                .build();

        post = postRepository.save(post);

        // Save media
        if (hasMedia) {
            final Post savedPost = post;
            List<PostMedia> mediaEntities = request.getMediaList().stream()
                    .map(m -> PostMedia.builder()
                            .post(savedPost)
                            .mediaUrl(m.getMediaUrl())
                            .mediaType(m.getMediaType())
                            .thumbnailUrl(m.getThumbnailUrl())
                            .displayOrder(m.getDisplayOrder())
                            .fileSize(m.getFileSize())
                            .mimeType(m.getMimeType())
                            .width(m.getWidth())
                            .height(m.getHeight())
                            .duration(m.getDuration())
                            .build())
                    .collect(Collectors.toList());
            postMediaRepository.saveAll(mediaEntities);
            post.setMediaList(mediaEntities);
        }

        // Extract and save hashtags
        processHashtags(post, request.getContent());

        // Extract and save mentions
        processMentions(post, request.getContent());

        // Reload with relations
        post = postRepository.findById(post.getId()).orElse(post);

        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(authUserId);
        PostResponse response = postMapper.toPostResponse(post, author, authUserId);
        response.setLikedByMe(false);
        response.setSavedByMe(false);

        return DataResponseMessage.success("Post created successfully.", response);
    }

    @Override
    @Transactional
    public DataResponseMessage<PostResponse> updatePost(Long authUserId, Long postId, UpdatePostRequest request) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new InactivePostException(postId);
        }
        if (!post.getAuthUserId().equals(authUserId)) {
            throw new PostAccessDeniedException("Only the post owner can update the post.");
        }

        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getVisibility() != null) post.setVisibility(request.getVisibility());
        if (request.getCommentsEnabled() != null) post.setCommentsEnabled(request.getCommentsEnabled());
        if (request.getLikesEnabled() != null) post.setLikesEnabled(request.getLikesEnabled());

        // Update media if provided
        if (request.getMediaList() != null) {
            postMediaRepository.deleteByPostId(postId);
            final Post finalPost = post;
            List<PostMedia> newMedia = request.getMediaList().stream()
                    .map(m -> PostMedia.builder()
                            .post(finalPost)
                            .mediaUrl(m.getMediaUrl())
                            .mediaType(m.getMediaType())
                            .thumbnailUrl(m.getThumbnailUrl())
                            .displayOrder(m.getDisplayOrder())
                            .fileSize(m.getFileSize())
                            .mimeType(m.getMimeType())
                            .width(m.getWidth())
                            .height(m.getHeight())
                            .duration(m.getDuration())
                            .build())
                    .collect(Collectors.toList());
            postMediaRepository.saveAll(newMedia);

            PostType postType = postTypeResolver.resolvePostType(post.getContent(), request.getMediaList());
            post.setPostType(postType);
        }

        // Re-process hashtags and mentions
        postHashtagRepository.deleteByPostId(postId);
        mentionRepository.deleteByPostId(postId);
        processHashtags(post, post.getContent());
        processMentions(post, post.getContent());

        post = postRepository.save(post);
        post = postRepository.findById(post.getId()).orElse(post);

        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(authUserId);
        PostResponse response = postMapper.toPostResponse(post, author, authUserId);
        enrichWithUserInteractions(response, postId, authUserId);

        return DataResponseMessage.success("Post updated successfully.", response);
    }

    @Override
    @Transactional
    public ResponseMessage deletePost(Long authUserId, Long postId) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getAuthUserId().equals(authUserId)) {
            throw new PostAccessDeniedException("Only the post owner can delete the post.");
        }

        post.softDelete();
        postRepository.save(post);

        return ResponseMessage.success("Post deleted successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PostDetailResponse> getPostDetail(Long authUserId, Long postId) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(post.getAuthUserId());

        // Get latest comments (top-level, ACTIVE, up to 5)
        Page<Comment> latestCommentPage = commentRepository.findByPostIdAndParentCommentIsNullAndStatus(
                postId, CommentStatus.ACTIVE, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<CommentResponse> latestComments = latestCommentPage.getContent().stream()
                .map(comment -> {
                    UserSummaryResponse commentAuthor = userServiceClient.getUserByAuthUserId(comment.getAuthUserId());
                    CommentResponse cr = commentMapper.toCommentResponse(comment, commentAuthor, authUserId);
                    if (authUserId != null) {
                        // likedByMe for comment - simplified
                        cr.setLikedByMe(false);
                    }
                    return cr;
                })
                .collect(Collectors.toList());

        PostDetailResponse detail = postMapper.toPostDetailResponse(post, author, latestComments, authUserId);

        // Enrich user interactions
        if (authUserId != null) {
            boolean likedByMe = postLikeRepository.existsByPostIdAndAuthUserId(postId, authUserId);
            boolean savedByMe = savedPostRepository.existsByPostIdAndAuthUserId(postId, authUserId);
            detail.setLikedByMe(likedByMe);
            detail.setSavedByMe(savedByMe);
            if (likedByMe) {
                postLikeRepository.findByPostIdAndAuthUserId(postId, authUserId)
                        .ifPresent(like -> detail.setMyReaction(like.getReactionType()));
            }
        }

        return DataResponseMessage.success("Post detail retrieved successfully.", detail);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<PostResponse>> getFeed(Long authUserId, int page, int size, SortType sortType) {
        Sort sort = buildSort(sortType);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postRepository.findFeedPosts(pageable);
        Page<PostResponse> mapped = posts.map(p -> enrichPostResponse(p, authUserId));
        return DataResponseMessage.success("Feed retrieved successfully.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<PostResponse>> getMyPosts(Long authUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findByAuthUserIdAndStatus(authUserId, PostStatus.ACTIVE, pageable);
        Page<PostResponse> mapped = posts.map(p -> enrichPostResponse(p, authUserId));
        return DataResponseMessage.success("My posts retrieved successfully.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<PostResponse>> getUserPublicPosts(Long requesterAuthUserId, Long targetAuthUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findByAuthUserIdAndStatusAndVisibility(
                targetAuthUserId, PostStatus.ACTIVE, PostVisibility.PUBLIC, pageable);
        Page<PostResponse> mapped = posts.map(p -> enrichPostResponse(p, requesterAuthUserId));
        return DataResponseMessage.success("User public posts retrieved successfully.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<PostResponse>> searchPosts(Long authUserId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.searchByKeyword(keyword, pageable);
        Page<PostResponse> mapped = posts.map(p -> enrichPostResponse(p, authUserId));
        return DataResponseMessage.success("Search results retrieved.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PostStatsResponse> getPostStats(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return DataResponseMessage.success("Post stats retrieved.", postMapper.toPostStatsResponse(post));
    }

    @Override
    @Transactional
    public ResponseMessage archivePost(Long authUserId, Long postId) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));
        if (!post.getAuthUserId().equals(authUserId)) {
            throw new PostAccessDeniedException("Only the post owner can archive the post.");
        }
        post.setStatus(PostStatus.ARCHIVED);
        postRepository.save(post);
        return ResponseMessage.success("Post archived successfully.");
    }

    @Override
    @Transactional
    public ResponseMessage pinPost(Long authUserId, Long postId) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));
        if (!post.getAuthUserId().equals(authUserId)) {
            throw new PostAccessDeniedException("Only the post owner can pin the post.");
        }
        post.setPinned(true);
        postRepository.save(post);
        return ResponseMessage.success("Post pinned successfully.");
    }

    @Override
    @Transactional
    public ResponseMessage unpinPost(Long authUserId, Long postId) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));
        if (!post.getAuthUserId().equals(authUserId)) {
            throw new PostAccessDeniedException("Only the post owner can unpin the post.");
        }
        post.setPinned(false);
        postRepository.save(post);
        return ResponseMessage.success("Post unpinned successfully.");
    }

    @Override
    @Transactional
    public DataResponseMessage<PostResponse> updatePostStatus(Long postId, UpdatePostStatusRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        post.setStatus(request.getStatus());
        post = postRepository.save(post);
        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(post.getAuthUserId());
        PostResponse response = postMapper.toPostResponse(post, author, null);
        return DataResponseMessage.success("Post status updated successfully.", response);
    }

    // ==================== Helpers ====================

    private void processHashtags(Post post, String content) {
        List<String> tagNames = hashtagExtractor.extractHashtags(content);
        for (String tagName : tagNames) {
            Hashtag hashtag = hashtagRepository.findByName(tagName)
                    .orElseGet(() -> hashtagRepository.save(Hashtag.builder().name(tagName).build()));
            hashtag.incrementUsageCount();
            hashtagRepository.save(hashtag);

            postHashtagRepository.save(PostHashtag.builder()
                    .post(post)
                    .hashtag(hashtag)
                    .build());
        }
    }

    private void processMentions(Post post, String content) {
        List<String> usernames = mentionExtractor.extractMentions(content);
        for (String username : usernames) {
            Long mentionedUserId = null;
            try {
                UserSummaryResponse user = userServiceClient.getUserByUsername(username);
                if (user != null && user.getAuthUserId() != null) {
                    mentionedUserId = user.getAuthUserId();
                }
            } catch (Exception e) {
                log.warn("Could not resolve authUserId for mentioned username: {}", username);
            }
            mentionRepository.save(Mention.builder()
                    .post(post)
                    .username(username)
                    .mentionedAuthUserId(mentionedUserId)
                    .build());
        }
    }

    private PostResponse enrichPostResponse(Post post, Long authUserId) {
        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(post.getAuthUserId());
        PostResponse response = postMapper.toPostResponse(post, author, authUserId);
        if (authUserId != null) {
            enrichWithUserInteractions(response, post.getId(), authUserId);
        }
        return response;
    }

    private void enrichWithUserInteractions(PostResponse response, Long postId, Long authUserId) {
        boolean likedByMe = postLikeRepository.existsByPostIdAndAuthUserId(postId, authUserId);
        boolean savedByMe = savedPostRepository.existsByPostIdAndAuthUserId(postId, authUserId);
        response.setLikedByMe(likedByMe);
        response.setSavedByMe(savedByMe);
        if (likedByMe) {
            postLikeRepository.findByPostIdAndAuthUserId(postId, authUserId)
                    .ifPresent(like -> response.setMyReaction(like.getReactionType()));
        }
    }

    private Sort buildSort(SortType sortType) {
        if (sortType == null) return Sort.by(Sort.Direction.DESC, "createdAt");
        return switch (sortType) {
            case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
            case MOST_LIKED -> Sort.by(Sort.Direction.DESC, "likeCount");
            case MOST_COMMENTED -> Sort.by(Sort.Direction.DESC, "commentCount");
            case MOST_SAVED -> Sort.by(Sort.Direction.DESC, "saveCount");
        };
    }
}
