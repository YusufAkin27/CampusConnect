package admin_service.service;

import admin_service.dto.request.CreateAdminRequest;
import admin_service.dto.response.AdminUserDetailResponse;
import admin_service.entity.AdminUser;
import admin_service.enums.AdminRole;
import admin_service.enums.AdminStatus;
import admin_service.exception.AdminAlreadyExistsException;
import admin_service.mapper.AdminUserMapper;
import admin_service.repository.AdminUserRepository;
import admin_service.service.impl.AdminUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private AdminUserMapper adminUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void createAdmin_Success() {
        CreateAdminRequest request = CreateAdminRequest.builder()
                .username("admin1")
                .email("admin@test.com")
                .password("password123")
                .fullName("Test Admin")
                .role(AdminRole.ADMIN)
                .build();

        AdminUser saved = AdminUser.builder()
                .id(1L).username("admin1").email("admin@test.com")
                .fullName("Test Admin").role(AdminRole.ADMIN).status(AdminStatus.ACTIVE)
                .createdAt(LocalDateTime.now()).build();

        AdminUserDetailResponse expected = AdminUserDetailResponse.builder()
                .id(1L).username("admin1").email("admin@test.com")
                .fullName("Test Admin").role(AdminRole.ADMIN).build();

        when(adminUserRepository.existsByUsername("admin1")).thenReturn(false);
        when(adminUserRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(adminUserRepository.save(any(AdminUser.class))).thenReturn(saved);
        when(adminUserMapper.toDetailResponse(saved)).thenReturn(expected);

        AdminUserDetailResponse result = adminUserService.createAdmin(request);

        assertNotNull(result);
        assertEquals("admin1", result.getUsername());
        assertEquals(AdminRole.ADMIN, result.getRole());
        verify(adminUserRepository).save(any(AdminUser.class));
    }

    @Test
    void createAdmin_DuplicateUsername_ThrowsException() {
        CreateAdminRequest request = CreateAdminRequest.builder()
                .username("admin1").email("admin@test.com")
                .password("pass").fullName("Test").role(AdminRole.ADMIN).build();

        when(adminUserRepository.existsByUsername("admin1")).thenReturn(true);

        assertThrows(AdminAlreadyExistsException.class, () -> adminUserService.createAdmin(request));
        verify(adminUserRepository, never()).save(any());
    }
}
