package logging_service.util;

import org.springframework.stereotype.Component;

/**
 * Utility for truncating long strings to prevent excessive database storage.
 */
@Component
public class LogTruncationUtil {

    public static final int MAX_MESSAGE_LENGTH = 2000;
    public static final int MAX_DETAILS_LENGTH = 5000;
    public static final int MAX_STACK_TRACE_LENGTH = 10000;
    public static final int MAX_BODY_PREVIEW_LENGTH = 2000;
    public static final int MAX_METADATA_LENGTH = 5000;
    public static final int MAX_EXCEPTION_MESSAGE_LENGTH = 2000;
    public static final int MAX_OLD_NEW_VALUE_LENGTH = 3000;

    private static final String TRUNCATION_SUFFIX = "... [TRUNCATED]";

    /**
     * Truncates a string to the given max length.
     * If the string is null or shorter than maxLength, returns as-is.
     *
     * @param value     the string to truncate
     * @param maxLength maximum allowed length
     * @return truncated string with suffix, or original string
     */
    public String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - TRUNCATION_SUFFIX.length()) + TRUNCATION_SUFFIX;
    }

    public String truncateMessage(String message) {
        return truncate(message, MAX_MESSAGE_LENGTH);
    }

    public String truncateDetails(String details) {
        return truncate(details, MAX_DETAILS_LENGTH);
    }

    public String truncateStackTrace(String stackTrace) {
        return truncate(stackTrace, MAX_STACK_TRACE_LENGTH);
    }

    public String truncateBodyPreview(String body) {
        return truncate(body, MAX_BODY_PREVIEW_LENGTH);
    }

    public String truncateMetadata(String metadata) {
        return truncate(metadata, MAX_METADATA_LENGTH);
    }

    public String truncateExceptionMessage(String exceptionMessage) {
        return truncate(exceptionMessage, MAX_EXCEPTION_MESSAGE_LENGTH);
    }

    public String truncateOldNewValue(String value) {
        return truncate(value, MAX_OLD_NEW_VALUE_LENGTH);
    }
}
