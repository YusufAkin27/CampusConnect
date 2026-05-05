package post_service.util;

import org.springframework.stereotype.Component;
import post_service.dto.request.PostMediaRequest;
import post_service.enums.MediaType;
import post_service.enums.PostType;

import java.util.List;

/**
 * Resolves the PostType based on content and media list.
 */
@Component
public class PostTypeResolver {

    /**
     * Determines PostType from the content and media list.
     *
     * @param content   the post content (may be null or blank)
     * @param mediaList the list of media requests (may be null or empty)
     * @return the resolved PostType
     */
    public PostType resolvePostType(String content, List<PostMediaRequest> mediaList) {
        boolean hasText = content != null && !content.isBlank();

        if (mediaList == null || mediaList.isEmpty()) {
            return PostType.TEXT;
        }

        boolean hasImage = mediaList.stream().anyMatch(m -> MediaType.IMAGE.equals(m.getMediaType()) || MediaType.GIF.equals(m.getMediaType()));
        boolean hasVideo = mediaList.stream().anyMatch(m -> MediaType.VIDEO.equals(m.getMediaType()));

        if (hasImage && hasVideo) {
            return PostType.MIXED;
        }
        if (hasVideo) {
            return PostType.VIDEO;
        }
        if (hasImage) {
            return PostType.IMAGE;
        }

        // FILE or other types
        return PostType.MIXED;
    }
}
