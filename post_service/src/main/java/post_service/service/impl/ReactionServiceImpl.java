package post_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import post_service.client.UserServiceClient;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.ResponseMessage;
import post_service.dto.request.ReactCommentRequest;
import post_service.dto.request.ReactPostRequest;
import post_service.dto.response.CommentResponse;
import post_service.dto.response.PostResponse;
import post_service.dto.response.UserSummaryResponse;
import post_service.entity.Comment;
import post_service.entity.CommentLike;
import post_service.entity.Post;
import post_service.entity.PostLike;
import post_service.enums.CommentStatus;
import post_service.enums.PostStatus;
import post_service.exception.*;
import post_service.mapper.CommentMapper;
import post_service.mapper.PostMapper;
import post_service.repository.*;
import post_service.service.ReactionService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final SavedPostRepository savedPostRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public DataResponseMessage<PostResponse> reactToPost(Long authUserId, Long postId, ReactPostRequest request) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new InactivePostException(postId);
        }
        if (!Boolean.TRUE.equals(post.getLikesEnabled())) {
            throw new LikesDisabledException();
        }

        postLikeRepository.findByPostIdAndAuthUserId(postId, authUserId)
                .ifPresentOrElse(
                        existingLike -> {
                            if (existingLike.getReactionType() == request.getReactionType()) {
                                // Same reaction, no-op
                                return;
                            }
                            // Update reaction type
                            existingLike.setReactionType(request.getReactionType());
                            postLikeRepository.save(existingLike);
                        },
                        () -> {
                            // New reaction
                            PostLike like = PostLike.builder()
                                    .post(post)
                                    .authUserId(authUserId)
                                    .reactionType(request.getReactionType())
                                    .build();
                            postLikeRepository.save(like);
                            post.incrementLikeCount();
                            postRepository.save(post);
                        }
                );

        Post updatedPost = postRepository.findById(postId).orElse(post);
        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(updatedPost.getAuthUserId());
        PostResponse response = postMapper.toPostResponse(updatedPost, author, authUserId);

        boolean likedByMe = postLikeRepository.existsByPostIdAndAuthUserId(postId, authUserId);
        boolean savedByMe = savedPostRepository.existsByPostIdAndAuthUserId(postId, authUserId);
        response.setLikedByMe(likedByMe);
        response.setSavedByMe(savedByMe);
        postLikeRepository.findByPostIdAndAuthUserId(postId, authUserId)
                .ifPresent(like -> response.setMyReaction(like.getReactionType()));

        return DataResponseMessage.success("Reaction applied successfully.", response);
    }

    @Override
    @Transactional
    public ResponseMessage removePostReaction(Long authUserId, Long postId) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!postLikeRepository.existsByPostIdAndAuthUserId(postId, authUserId)) {
            return ResponseMessage.success("No reaction to remove.");
        }

        postLikeRepository.deleteByPostIdAndAuthUserId(postId, authUserId);
        post.decrementLikeCount();
        postRepository.save(post);

        return ResponseMessage.success("Reaction removed successfully.");
    }

    @Override
    @Transactional
    public DataResponseMessage<CommentResponse> reactToComment(Long authUserId, Long commentId, ReactCommentRequest request) {
        Comment comment = commentRepository.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (comment.getStatus() != CommentStatus.ACTIVE) {
            throw new InactiveCommentException(commentId);
        }

        commentLikeRepository.findByCommentIdAndAuthUserId(commentId, authUserId)
                .ifPresentOrElse(
                        existingLike -> {
                            if (existingLike.getReactionType() != request.getReactionType()) {
                                existingLike.setReactionType(request.getReactionType());
                                commentLikeRepository.save(existingLike);
                            }
                        },
                        () -> {
                            CommentLike like = CommentLike.builder()
                                    .comment(comment)
                                    .authUserId(authUserId)
                                    .reactionType(request.getReactionType())
                                    .build();
                            commentLikeRepository.save(like);
                            comment.incrementLikeCount();
                            commentRepository.save(comment);
                        }
                );

        Comment updated = commentRepository.findById(commentId).orElse(comment);
        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(updated.getAuthUserId());
        CommentResponse response = commentMapper.toCommentResponse(updated, author, authUserId);

        boolean likedByMe = commentLikeRepository.existsByCommentIdAndAuthUserId(commentId, authUserId);
        response.setLikedByMe(likedByMe);
        commentLikeRepository.findByCommentIdAndAuthUserId(commentId, authUserId)
                .ifPresent(like -> response.setMyReaction(like.getReactionType()));

        return DataResponseMessage.success("Comment reaction applied successfully.", response);
    }

    @Override
    @Transactional
    public ResponseMessage removeCommentReaction(Long authUserId, Long commentId) {
        Comment comment = commentRepository.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!commentLikeRepository.existsByCommentIdAndAuthUserId(commentId, authUserId)) {
            return ResponseMessage.success("No reaction to remove.");
        }

        commentLikeRepository.deleteByCommentIdAndAuthUserId(commentId, authUserId);
        comment.decrementLikeCount();
        commentRepository.save(comment);

        return ResponseMessage.success("Comment reaction removed successfully.");
    }
}
