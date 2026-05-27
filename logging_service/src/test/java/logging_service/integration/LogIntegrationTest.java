package logging_service.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import logging_service.dto.request.BatchLogEntryRequest;
import logging_service.dto.request.CreateLogEntryRequest;
import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createLog_thenGetById_returnsPersistedLog() throws Exception {
        CreateLogEntryRequest request = CreateLogEntryRequest.builder()
                .serviceName("auth-service")
                .level(LogLevel.INFO)
                .category(LogCategory.APPLICATION)
                .message("Auth service started")
                .build();

        MvcResult result = mockMvc.perform(post("/v1/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long id = root.path("data").path("id").asLong();
        assertThat(id).isPositive();

        mockMvc.perform(get("/v1/api/logs/query/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.serviceName").value("auth-service"));
    }

    @Test
    void createBatchLogs_thenQueryByService_returnsResults() throws Exception {
        CreateLogEntryRequest first = CreateLogEntryRequest.builder()
                .serviceName("user-service")
                .level(LogLevel.INFO)
                .category(LogCategory.APPLICATION)
                .message("User created")
                .build();

        CreateLogEntryRequest second = CreateLogEntryRequest.builder()
                .serviceName("user-service")
                .level(LogLevel.WARN)
                .category(LogCategory.APPLICATION)
                .message("Profile incomplete")
                .build();

        BatchLogEntryRequest batch = BatchLogEntryRequest.builder()
                .logs(List.of(first, second))
                .build();

        mockMvc.perform(post("/v1/api/logs/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batch)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.receivedCount").value(2))
                .andExpect(jsonPath("$.data.savedCount").value(2))
                .andExpect(jsonPath("$.data.failedCount").value(0));

        mockMvc.perform(get("/v1/api/logs/query/service/{serviceName}", "user-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].serviceName").value("user-service"));
    }

    @Test
    void errorLogsEndpoint_returnsErrorLevelEntries() throws Exception {
        CreateLogEntryRequest errorLog = CreateLogEntryRequest.builder()
                .serviceName("post-service")
                .level(LogLevel.ERROR)
                .category(LogCategory.ERROR)
                .message("Unhandled exception")
                .build();

        mockMvc.perform(post("/v1/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(errorLog)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/api/logs/query/errors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].level").value("ERROR"));
    }
}

