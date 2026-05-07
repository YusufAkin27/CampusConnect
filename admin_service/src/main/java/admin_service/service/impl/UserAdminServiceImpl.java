package admin_service.service.impl;

import admin_service.client.FriendServiceClient;
import admin_service.client.PostServiceClient;
import admin_service.client.UserServiceClient;
import admin_service.dto.request.BanUserRequest;
import admin_service.dto.request.UpdateUserStatusRequest;
import admin_service.dto.response.UserBanRecordResponse;
import admin_service.entity.UserBanRecord;
import admin_service.enums.ActionType;
import admin_service.enums.BanType;
import admin_service.enums.TargetType;
import admin_service.exception.UserAlreadyBannedException;
import admin_service.exception.UserBanRecordNotFoundException;
import admin_service.mapper.UserBanRecordMapper;
import admin_service.repository.UserBanRecordRepository;
import admin_service.security.AdminAction;
import admin_service.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserAdminServiceImpl implements UserAdminService {

    private final UserServiceClient userServiceClient;
    private final PostServiceClient postServiceClient;
    private final FriendServiceClient friendServiceClient;
    private final UserBanRecordRepository banRecordRepository;
    private final UserBanRecordMapper banRecordMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllUsers(int page, int size) {
        log.debug("Fetching all users: page={}, size={}", page, size);
        return userServiceClient.getAllUsers(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserById(Long userId) {
        log.debug("Fetching user by id: {}", userId);
        return userServiceClient.getUserById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> searchUsers(String keyword, int page, int size) {
        log.debug("Searching users with keyword: {}", keyword);
        return userServiceClient.searchUsers(keyword, page, size);
    }

    @Override
    @AdminAction(actionType = ActionType.USER_STATUS_CHANGED, targetType = TargetType.USER)
    public Map<String, Object> updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        log.info("Updating user {} status to {}", userId, request.getStatus());
        return userServiceClient.updateUserStatus(userId, Map.of("status", request.getStatus()));
    }

    @Override
    @AdminAction(actionType = ActionType.USER_BANNED, targetType = TargetType.USER)
    public UserBanRecordResponse banUser(Long userId, BanUserRequest request, Long adminId) {
        if (banRecordRepository.existsByUserIdAndActiveTrue(userId)) {
            throw new UserAlreadyBannedException("User " + userId + " is already banned.");
        }

        UserBanRecord banRecord = UserBanRecord.builder()
                .userId(userId)
                .bannedByAdminId(adminId)
                .reason(request.getReason())
                .banType(request.getBanType())
                .expiresAt(request.getBanType() == BanType.TEMPORARY ? request.getExpiresAt() : null)
                .active(true)
                .build();

        UserBanRecord saved = banRecordRepository.save(banRecord);

        // Notify user-service about the ban
        try {
            userServiceClient.updateUserStatus(userId, Map.of("status", "BANNED"));
        } catch (Exception e) {
            log.error("Failed to update user status in user-service for ban: {}", e.getMessage());
        }

        log.info("User {} banned by admin {} - type: {}, reason: {}", userId, adminId, request.getBanType(), request.getReason());
        return banRecordMapper.toResponse(saved);
    }

    @Override
    @AdminAction(actionType = ActionType.USER_UNBANNED, targetType = TargetType.USER)
    public UserBanRecordResponse unbanUser(Long userId, Long adminId) {
        UserBanRecord activeBan = banRecordRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new UserBanRecordNotFoundException("No active ban found for user: " + userId));

        activeBan.setActive(false);
        UserBanRecord saved = banRecordRepository.save(activeBan);

        // Notify user-service about the unban
        try {
            userServiceClient.updateUserStatus(userId, Map.of("status", "ACTIVE"));
        } catch (Exception e) {
            log.error("Failed to update user status in user-service for unban: {}", e.getMessage());
        }

        log.info("User {} unbanned by admin {}", userId, adminId);
        return banRecordMapper.toResponse(saved);
    }

    @Override
    @AdminAction(actionType = ActionType.USER_DELETED, targetType = TargetType.USER)
    public Map<String, Object> deleteUser(Long userId) {
        log.info("Deleting user: {}", userId);
        return userServiceClient.deleteUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserPosts(Long userId, int page, int size) {
        return postServiceClient.getPostsByUser(userId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserFriends(Long userId, int page, int size) {
        return friendServiceClient.getUserFriends(userId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBanRecordResponse> getUserBanHistory(Long userId) {
        return banRecordRepository.findByUserId(userId).stream()
                .map(banRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserActivity(Long userId) {
        return userServiceClient.getUserActivity(userId);
    }
}
