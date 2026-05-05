package post_service.util;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to extract mentions from post content.
 * Mentions are extracted without the leading '@'.
 */
@Component
public class MentionExtractor {

    private static final Pattern MENTION_PATTERN = Pattern.compile("(?<![\\w@])@([\\w]{1,50})");

    /**
     * Extracts unique mentioned usernames from content.
     *
     * @param content the post content
     * @return a list of unique usernames (without '@')
     */
    public List<String> extractMentions(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        Set<String> mentions = new LinkedHashSet<>();
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while (matcher.find()) {
            mentions.add(matcher.group(1));
        }
        return List.copyOf(mentions);
    }
}
