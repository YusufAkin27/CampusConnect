package admin_service.service;

import java.util.Map;

public interface PostAdminService {

    Map<String, Object> getAllPosts(int page, int size);

    Map<String, Object> getPostById(Long postId);

    Map<String, Object> deletePost(Long postId);

    Map<String, Object> hidePost(Long postId);

    Map<String, Object> unhidePost(Long postId);

    Map<String, Object> getReportedPosts(int page, int size);

    Map<String, Object> getPostsByUser(Long userId, int page, int size);
}
