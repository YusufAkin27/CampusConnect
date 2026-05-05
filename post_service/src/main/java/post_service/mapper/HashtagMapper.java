package post_service.mapper;

import org.springframework.stereotype.Component;
import post_service.dto.response.HashtagResponse;
import post_service.entity.Hashtag;

/**
 * Manual mapper for Hashtag entity to HashtagResponse DTO.
 */
@Component
public class HashtagMapper {

    public HashtagResponse toHashtagResponse(Hashtag hashtag) {
        if (hashtag == null) return null;
        return HashtagResponse.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .usageCount(hashtag.getUsageCount())
                .build();
    }
}
