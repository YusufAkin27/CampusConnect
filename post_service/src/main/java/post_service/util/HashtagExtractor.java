package post_service.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class to extract hashtags from post content.
 * Hashtags are extracted in lowercase without the leading '#'.
 */
@Component
public class HashtagExtractor {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("(?<![\\w#])#([\\w\\u0080-\\uFFFF]{1,50})");

    /**
     * Extracts unique hashtags from content.
     *
     * @param content the post content
     * @return a list of lowercase hashtag names (without '#')
     */
    public List<String> extractHashtags(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        Set<String> hashtags = new java.util.LinkedHashSet<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(content);
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();
            hashtags.add(tag);
        }
        return List.copyOf(hashtags);
    }
}
