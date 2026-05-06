package media_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for calculating file checksums.
 *
 * Uses SHA-256 hashing for duplicate file detection.
 * This is an optional feature for v1 but the structure is in place.
 *
 * TODO: Integrate checksum-based duplicate detection during upload to prevent
 *       storing the same file multiple times.
 */
@Slf4j
@Component
public class ChecksumUtil {

    private static final String ALGORITHM = "SHA-256";

    /**
     * Calculates the SHA-256 checksum of a MultipartFile's content.
     *
     * @param file the uploaded file
     * @return hex-encoded SHA-256 hash, or null if calculation fails
     */
    public String calculateChecksum(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try (InputStream is = file.getInputStream()) {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available: {}", e.getMessage());
            return null;
        } catch (IOException e) {
            log.warn("Could not calculate checksum for file '{}': {}", file.getOriginalFilename(), e.getMessage());
            return null;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
