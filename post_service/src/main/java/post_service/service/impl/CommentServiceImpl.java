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
import post_service.dto.request.CreateCommentRequest;
import post_service.dto.request.UpdateCommentRequest;
import post_service.dto.response.CommentResponse;
import post_service.dto.response.UserSummaryResponse;
import post_service.entity.Comment;
import post_service.entity.Post;
import post_service.enums.CommentStatus;
import post_service.enums.PostStatus;
import post_service.exception.*;
import post_service.mapper.CommentMapper;
import post_service.repository.CommentLikeRepository;
import post_service.repository.CommentRepository;
import post_service.repository.PostRepository;
import post_service.service.CommentService;
import post_service.util.PageResponseConverter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional
    public DataResponseMessage<CommentResponse> createComment(Long authUserId, Long postId, CreateCommentRequest request) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new InactivePostException(postId);
        }
        if (!Boolean.TRUE.equals(post.getCommentsEnabled())) {
            throw new CommentsDisabledException();
        }

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findByIdAndStatusNot(request.getParentCommentId(), CommentStatus.DELETED)
                    .orElseThrow(() -> new CommentNotFoundException(request.getParentCommentId()));
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new InvalidCommentDataException("Parent comment does not belong to this post.");
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .authUserId(authUserId)
                .parentComment(parentComment)
                .content(request.getContent())
                .build();

        comment = commentRepository.save(comment);

        // Update counts
        post.incrementCommentCount();
        if (parentComment != null) {
            parentComment.incrementReplyCount();
            commentRepository.save(parentComment);
        }
        postRepository.save(post);

        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(authUserId);
        CommentResponse response = commentMapper.toCommentResponse(comment, author, authUserId);

        return DataResponseMessage.success("Comment created successfully.", response);
    }

    @Override
    @Transactional
    public DataResponseMessage<CommentResponse> updateComment(Long authUserId, Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (comment.getStatus() != CommentStatus.ACTIVE) {
            throw new InactiveCommentException(commentId);
        }
        if (!comment.getAuthUserId().equals(authUserId)) {
            throw new CommentAccessDeniedException("Only the comment owner can update the comment.");
        }

        comment.setContent(request.getContent());
        comment.setEdited(true);
        comment = commentRepository.save(comment);

        UserSummaryResponse author = userServiceClient.getUserByAuthUserId(authUserId);
        CommentResponse response = commentMapper.toCommentResponse(comment, author, authUserId);
        boolean likedByMe = commentLikeRepository.existsByCommentIdAndAuthUserId(commentId, authUserId);
        response.setLikedByMe(likedByMe);
        if (likedByMe) {
            commentLikeRepository.findByCommentIdAndAuthUserId(commentId, authUserId)
                    .ifPresent(like -> response.setMyReaction(like.getReactionType()));
        }

        return DataResponseMessage.success("Comment updated successfully.", response);
    }

    @Override
    @Transactional
    public ResponseMessage deleteComment(Long authUserId, Long commentId) {
        Comment comment = commentRepository.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getAuthUserId().equals(authUserId)) {
            throw new CommentAccessDeniedException("Only the comment owner can delete the comment.");
        }

        comment.softDelete();
        commentRepository.save(comment);

        // Update post commentCount
        Post post = comment.getPost();
        if (post != null) {
            post.decrementCommentCount();
            postRepository.save(post);
        }

        // Update parent replyCount if this is a reply
        if (comment.getParentComment() != null) {
            Comment parent = comment.getParentComment();
            parent.decrementReplyCount();
            commentRepository.save(parent);
        }

        return ResponseMessage.success("Comment deleted successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<CommentResponse>> getPostComments(Long authUserId, Long postId, int page, int size) {
        postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Page<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNullAndStatus(
                postId, CommentStatus.ACTIVE, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        Page<CommentResponse> mapped = comments.map(comment -> {
            UserSummaryResponse author = userServiceClient.getUserByAuthUserId(comment.getAuthUserId());
            CommentResponse cr = commentMapper.toCommentResponse(comment, author, authUserId);
            if (authUserId != null) {
                boolean likedByMe = commentLikeRepository.existsByCommentIdAndAuthUserId(comment.getId(), authUserId);
                cr.setLikedByMe(likedByMe);
                if (likedByMe) {
                    commentLikeRepository.findByCommentIdAndAuthUserId(comment.getId(), authUserId)
                            .ifPresent(like -> cr.setMyReaction(like.getReactionType()));
                }
            }
            return cr;
        });

        return DataResponseMessage.success("Comments retrieved successfully.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<CommentResponse>> getCommentReplies(Long authUserId, Long commentId, int page, int size) {
        commentRepository.findByIdAndStatusNot(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        Page<Comment> replies = commentRepository.findByParentCommentIdAndStatus(
                commentId, CommentStatus.ACTIVE, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt")));

        Page<CommentResponse> mapped = replies.map(reply -> {
            UserSummaryResponse author = userServiceClient.getUserByAuthUserId(reply.getAuthUserId());
            CommentResponse cr = commentMapper.toCommentResponse(reply, author, authUserId);
            if (authUserId != null) {
                boolean likedByMe = commentLikeRepository.existsByCommentIdAndAuthUserId(reply.getId(), authUserId);
                cr.setLikedByMe(likedByMe);
            }
            return cr;
        });

        return DataResponseMessage.success("Replies retrieved successfully.", pageResponseConverter.toPageResponse(mapped));
    }
}
