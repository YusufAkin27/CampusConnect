package user_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import user_service.common.response.DataResponseMessage;
import user_service.common.response.PageResponse;
import user_service.dto.response.UserSummaryResponse;
import user_service.service.UserProfileService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.consul.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:user_security_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
})
@AutoConfigureMockMvc
class UserSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @Test
    void searchEndpoint_ShouldBeAccessible_WithoutAuthentication() throws Exception {
        PageResponse<UserSummaryResponse> page = PageResponse.<UserSummaryResponse>builder()
                .content(List.of(UserSummaryResponse.builder().id(1L).build()))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();
        when(userProfileService.searchUsers(anyString(), anyInt(), anyInt()))
                .thenReturn(DataResponseMessage.success("ok", page));

        mockMvc.perform(get("/v1/api/users/search").param("keyword", "john"))
                .andExpect(status().isOk());
    }
}
