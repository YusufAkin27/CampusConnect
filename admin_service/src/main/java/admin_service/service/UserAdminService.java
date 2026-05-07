package admin_service.service;

import admin_service.dto.request.BanUserRequest;
import admin_service.dto.request.UpdateUserStatusRequest;
import admin_service.dto.response.UserBanRecordResponse;

import java.util.List;
import java.util.Map;

public interface UserAdminService {

    Map<String, Object> getAllUsers(int page, int size);

    Map<String, Object> getUserById(Long userId);

    Map<String, Object> searchUsers(String keyword, int page, int size);

    Map<String, Object> updateUserStatus(Long userId, UpdateUserStatusRequest request);

    UserBanRecordResponse banUser(Long userId, BanUserRequest request, Long adminId);

    UserBanRecordResponse unbanUser(Long userId, Long adminId);

    Map<String, Object> deleteUser(Long userId);

    Map<String, Object> getUserPosts(Long userId, int page, int size);

    Map<String, Object> getUserFriends(Long userId, int page, int size);

    List<UserBanRecordResponse> getUserBanHistory(Long userId);

    Map<String, Object> getUserActivity(Long userId);
}
