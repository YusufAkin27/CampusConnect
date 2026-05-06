package event_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import event_service.dto.request.CreateEventRequest;
import event_service.dto.response.EventParticipantResponse;
import event_service.dto.response.EventResponse;
import event_service.dto.response.EventSummaryResponse;
import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import event_service.enums.EventType;
import event_service.enums.EventVisibility;
import event_service.security.UserContext;
import event_service.security.UserContextResolver;
import event_service.service.EventService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserContextResolver userContextResolver;

    @Test
    void shouldCreateEvent() throws Exception {
        UserContext context = UserContext.builder().userId(10L).username("user").fullName("User").build();
        Mockito.when(userContextResolver.resolve(Mockito.any())).thenReturn(context);

        EventResponse response = EventResponse.builder().id(1L).title("Title").build();
        Mockito.when(eventService.createEvent(Mockito.any(), Mockito.any())).thenReturn(response);

        CreateEventRequest request = CreateEventRequest.builder()
            .title("Title")
            .description("Desc")
            .shortDescription("Short")
            .category(EventCategory.ACADEMIC)
            .type(EventType.PHYSICAL)
            .visibility(EventVisibility.PUBLIC)
            .startDateTime(LocalDateTime.now().plusDays(1))
            .endDateTime(LocalDateTime.now().plusDays(2))
            .requiresApproval(false)
            .build();

        mockMvc.perform(post("/v1/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetEvent() throws Exception {
        EventResponse response = EventResponse.builder().id(1L).title("Title").build();
        Mockito.when(eventService.getEventById(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/api/events/1"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSearchEvents() throws Exception {
        EventSummaryResponse summary = EventSummaryResponse.builder()
            .id(1L)
            .title("Title")
            .status(EventStatus.PUBLISHED)
            .build();
        Mockito.when(eventService.searchEvents(Mockito.any()))
            .thenReturn(new PageImpl<>(List.of(summary)));

        mockMvc.perform(get("/v1/api/events/search")
                .param("keyword", "test"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldJoinEvent() throws Exception {
        UserContext context = UserContext.builder().userId(10L).username("user").fullName("User").build();
        Mockito.when(userContextResolver.resolve(Mockito.any())).thenReturn(context);

        EventParticipantResponse response = EventParticipantResponse.builder().id(5L).eventId(1L).build();
        Mockito.when(eventService.joinEvent(Mockito.eq(1L), Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/v1/api/events/1/join"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldFavoriteEvent() throws Exception {
        UserContext context = UserContext.builder().userId(10L).build();
        Mockito.when(userContextResolver.resolve(Mockito.any())).thenReturn(context);

        mockMvc.perform(post("/v1/api/events/1/favorite"))
            .andExpect(status().isOk());
    }
}
