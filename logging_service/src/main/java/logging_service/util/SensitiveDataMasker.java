package logging_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for masking sensitive data in logs.
 * Prevents passwords, tokens, and secrets from being stored in plain text.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SensitiveDataMasker {

    private static final String MASK_VALUE = "***MASKED***";

    /**
     * Sensitive field names to be masked (case-insensitive).
     */
    private static final String[] SENSITIVE_FIELDS = {
            "password",
            "oldPassword",
            "newPassword",
            "confirmPassword",
            "token",
            "accessToken",
            "refreshToken",
            "authorization",
            "cookie",
            "set-cookie",
            "secret",
            "apiKey",
            "privateKey"
    };

    /**
     * Regex pattern that matches sensitive JSON key-value pairs.
     * Handles both quoted string values and numeric/boolean values.
     */
    private static final Pattern SENSITIVE_PATTERN;

    static {
        StringBuilder patternBuilder = new StringBuilder("(?i)(\"(");
        for (int i = 0; i < SENSITIVE_FIELDS.length; i++) {
            if (i > 0) patternBuilder.append("|");
            patternBuilder.append(Pattern.quote(SENSITIVE_FIELDS[i]));
        }
        patternBuilder.append(")\"\\s*:\\s*)\"[^\"]*\"");
        SENSITIVE_PATTERN = Pattern.compile(patternBuilder.toString());
    }

    /**
     * Masks sensitive fields in a JSON string or any string containing key:value pairs.
     *
     * @param input the raw string (may be JSON or other format)
     * @return string with sensitive values replaced by MASK_VALUE
     */
    public String mask(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        try {
            return SENSITIVE_PATTERN.matcher(input)
                    .replaceAll("$1\"" + MASK_VALUE + "\"");
        } catch (Exception e) {
            log.warn("Failed to mask sensitive data in string: {}", e.getMessage());
            return input;
        }
    }

    /**
     * Masks sensitive fields in a Map<String, Object>.
     *
     * @param metadata the map to process
     * @return a new map with sensitive values masked
     */
    public Map<String, Object> maskMap(Map<String, Object> metadata) {
        if (metadata == null) {
            return null;
        }
        Map<String, Object> maskedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (isSensitiveKey(entry.getKey())) {
                maskedMap.put(entry.getKey(), MASK_VALUE);
            } else if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) entry.getValue();
                maskedMap.put(entry.getKey(), maskMap(nestedMap));
            } else {
                maskedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return maskedMap;
    }

    /**
     * Converts a Map to a JSON string with sensitive fields masked.
     *
     * @param metadata the map to serialize
     * @param objectMapper Jackson ObjectMapper
     * @return JSON string with sensitive fields masked, or null on error
     */
    public String maskAndSerialize(Map<String, Object> metadata, ObjectMapper objectMapper) {
        if (metadata == null) {
            return null;
        }
        try {
            Map<String, Object> masked = maskMap(metadata);
            return objectMapper.writeValueAsString(masked);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize metadata map: {}", e.getMessage());
            return null;
        }
    }

    private boolean isSensitiveKey(String key) {
        if (key == null) return false;
        String lowerKey = key.toLowerCase();
        for (String sensitiveField : SENSITIVE_FIELDS) {
            if (lowerKey.equals(sensitiveField.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
