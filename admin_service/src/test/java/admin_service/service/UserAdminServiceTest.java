package admin_service.service;

import admin_service.dto.request.BanUserRequest;
import admin_service.dto.response.UserBanRecordResponse;
import admin_service.client.FriendServiceClient;
import admin_service.client.PostServiceClient;
import admin_service.client.UserServiceClient;
import admin_service.entity.UserBanRecord;
import admin_service.enums.BanType;
import admin_service.exception.UserAlreadyBannedException;
import admin_service.mapper.UserBanRecordMapper;
import admin_service.repository.UserBanRecordRepository;
import admin_service.service.impl.UserAdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @InjectMocks
    private UserAdminServiceImpl userAdminService;

    @Mock private UserServiceClient userServiceClient;
    @Mock private PostServiceClient postServiceClient;
    @Mock private FriendServiceClient friendServiceClient;
    @Mock private UserBanRecordRepository banRecordRepository;
    @Mock private UserBanRecordMapper banRecordMapper;

    @Test
    void banUser_Success() {
        BanUserRequest request = BanUserRequest.builder()
                .reason("Spam behavior").banType(BanType.PERMANENT).build();

        UserBanRecord saved = UserBanRecord.builder()
                .id(1L).userId(100L).bannedByAdminId(1L)
                .reason("Spam behavior").banType(BanType.PERMANENT)
                .active(true).createdAt(LocalDateTime.now()).build();

        UserBanRecordResponse expected = UserBanRecordResponse.builder()
                .id(1L).userId(100L).banType(BanType.PERMANENT).active(true).build();

        when(banRecordRepository.existsByUserIdAndActiveTrue(100L)).thenReturn(false);
        when(banRecordRepository.save(any())).thenReturn(saved);
        when(banRecordMapper.toResponse(saved)).thenReturn(expected);

        UserBanRecordResponse result = userAdminService.banUser(100L, request, 1L);

        assertNotNull(result);
        assertTrue(result.getActive());
        assertEquals(BanType.PERMANENT, result.getBanType());
    }

    @Test
    void banUser_AlreadyBanned_ThrowsException() {
        when(banRecordRepository.existsByUserIdAndActiveTrue(100L)).thenReturn(true);

        BanUserRequest request = BanUserRequest.builder()
                .reason("test").banType(BanType.PERMANENT).build();

        assertThrows(UserAlreadyBannedException.class, () -> userAdminService.banUser(100L, request, 1L));
    }
}
