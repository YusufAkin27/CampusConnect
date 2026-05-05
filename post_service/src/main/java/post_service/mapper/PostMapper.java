package post_service.mapper;

import org.springframework.stereotype.Component;
import post_service.dto.response.*;
import post_service.entity.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for Post entity to response DTOs.
 * likedByMe, savedByMe, myReaction are set externally in the service layer.
 */
@Component
public class PostMapper {

    public PostResponse toPostResponse(Post post, UserSummaryResponse author, Long currentAuthUserId) {
        if (post == null) return null;

        List<PostMediaResponse> mediaResponses = post.getMediaList() == null ? List.of()
                : post.getMediaList().stream().map(this::toPostMediaResponse).collect(Collectors.toList());

        List<String> hashtags = post.getPostHashtags() == null ? List.of()
                : post.getPostHashtags().stream()
                .map(ph -> ph.getHashtag().getName())
                .collect(Collectors.toList());

        List<MentionResponse> mentions = post.getMentions() == null ? List.of()
                : post.getMentions().stream()
                .map(m -> MentionResponse.builder()
                        .mentionedAuthUserId(m.getMentionedAuthUserId())
                        .username(m.getUsername())
                        .build())
                .collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .authUserId(post.getAuthUserId())
                .author(author)
                .content(post.getContent())
                .postType(post.getPostType())
                .visibility(post.getVisibility())
                .status(post.getStatus())
                .pinned(post.getPinned())
                .commentsEnabled(post.getCommentsEnabled())
                .likesEnabled(post.getLikesEnabled())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .saveCount(post.getSaveCount())
                .viewCount(post.getViewCount())
                .shareCount(post.getShareCount())
                .reportCount(post.getReportCount())
                .likedByMe(false)       // set by service
                .myReaction(null)       // set by service
                .savedByMe(false)       // set by service
                .mediaList(mediaResponses)
                .hashtags(hashtags)
                .mentions(mentions)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public PostDetailResponse toPostDetailResponse(Post post, UserSummaryResponse author,
                                                    List<CommentResponse> latestComments,
                                                    Long currentAuthUserId) {
        if (post == null) return null;

        PostResponse base = toPostResponse(post, author, currentAuthUserId);

        return PostDetailResponse.builder()
                .id(base.getId())
                .authUserId(base.getAuthUserId())
                .author(base.getAuthor())
                .content(base.getContent())
                .postType(base.getPostType())
                .visibility(base.getVisibility())
                .status(base.getStatus())
                .pinned(base.getPinned())
                .commentsEnabled(base.getCommentsEnabled())
                .likesEnabled(base.getLikesEnabled())
                .likeCount(base.getLikeCount())
                .commentCount(base.getCommentCount())
                .saveCount(base.getSaveCount())
                .viewCount(base.getViewCount())
                .shareCount(base.getShareCount())
                .reportCount(base.getReportCount())
                .likedByMe(base.getLikedByMe())
                .myReaction(base.getMyReaction())
                .savedByMe(base.getSavedByMe())
                .mediaList(base.getMediaList())
                .hashtags(base.getHashtags())
                .mentions(base.getMentions())
                .createdAt(base.getCreatedAt())
                .updatedAt(base.getUpdatedAt())
                .latestComments(latestComments)
                .build();
    }

    public PostSummaryResponse toPostSummaryResponse(Post post) {
        if (post == null) return null;

        String contentPreview = null;
        if (post.getContent() != null && !post.getContent().isBlank()) {
            contentPreview = post.getContent().length() > 150
                    ? post.getContent().substring(0, 150) + "..."
                    : post.getContent();
        }

        String firstMediaUrl = null;
        post_service.enums.MediaType firstMediaType = null;
        if (post.getMediaList() != null && !post.getMediaList().isEmpty()) {
            PostMedia firstMedia = post.getMediaList().get(0);
            firstMediaUrl = firstMedia.getMediaUrl();
            firstMediaType = firstMedia.getMediaType();
        }

        return PostSummaryResponse.builder()
                .id(post.getId())
                .authUserId(post.getAuthUserId())
                .contentPreview(contentPreview)
                .firstMediaUrl(firstMediaUrl)
                .firstMediaType(firstMediaType)
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .saveCount(post.getSaveCount())
                .visibility(post.getVisibility())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public PostMediaResponse toPostMediaResponse(PostMedia media) {
        if (media == null) return null;
        return PostMediaResponse.builder()
                .id(media.getId())
                .mediaUrl(media.getMediaUrl())
                .mediaType(media.getMediaType())
                .thumbnailUrl(media.getThumbnailUrl())
                .displayOrder(media.getDisplayOrder())
                .fileSize(media.getFileSize())
                .mimeType(media.getMimeType())
                .width(media.getWidth())
                .height(media.getHeight())
                .duration(media.getDuration())
                .build();
    }

    public PostStatsResponse toPostStatsResponse(Post post) {
        if (post == null) return null;
        return PostStatsResponse.builder()
                .postId(post.getId())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .saveCount(post.getSaveCount())
                .viewCount(post.getViewCount())
                .shareCount(post.getShareCount())
                .reportCount(post.getReportCount())
                .build();
    }
}
