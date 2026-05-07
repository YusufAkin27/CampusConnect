package admin_service.service.impl;

import admin_service.client.PostServiceClient;
import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import admin_service.security.AdminAction;
import admin_service.service.PostAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostAdminServiceImpl implements PostAdminService {

    private final PostServiceClient postServiceClient;

    @Override
    public Map<String, Object> getAllPosts(int page, int size) {
        log.debug("Fetching all posts: page={}, size={}", page, size);
        return postServiceClient.getAllPosts(page, size);
    }

    @Override
    public Map<String, Object> getPostById(Long postId) {
        log.debug("Fetching post by id: {}", postId);
        return postServiceClient.getPostById(postId);
    }

    @Override
    @AdminAction(actionType = ActionType.POST_DELETED, targetType = TargetType.POST)
    public Map<String, Object> deletePost(Long postId) {
        log.info("Deleting post: {}", postId);
        return postServiceClient.deletePost(postId);
    }

    @Override
    @AdminAction(actionType = ActionType.POST_HIDDEN, targetType = TargetType.POST)
    public Map<String, Object> hidePost(Long postId) {
        log.info("Hiding post: {}", postId);
        return postServiceClient.hidePost(postId);
    }

    @Override
    @AdminAction(actionType = ActionType.POST_UNHIDDEN, targetType = TargetType.POST)
    public Map<String, Object> unhidePost(Long postId) {
        log.info("Unhiding post: {}", postId);
        return postServiceClient.unhidePost(postId);
    }

    @Override
    public Map<String, Object> getReportedPosts(int page, int size) {
        return postServiceClient.getReportedPosts(page, size);
    }

    @Override
    public Map<String, Object> getPostsByUser(Long userId, int page, int size) {
        return postServiceClient.getPostsByUser(userId, page, size);
    }
}
