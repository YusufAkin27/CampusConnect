package api_gateway.util;

import java.util.List;

public final class PathUtils {

    private PathUtils() {
    }

    public static boolean matchesAny(String path, List<String> prefixes) {
        return prefixes.stream().anyMatch(path::startsWith);
    }
}
