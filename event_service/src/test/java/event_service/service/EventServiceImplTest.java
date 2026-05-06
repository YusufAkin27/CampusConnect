package event_service.service;

import event_service.client.LoggingClient;
import event_service.client.MediaServiceClient;
import event_service.client.dto.MediaSummaryResponse;
import event_service.client.dto.ValidateMediaResponse;
import event_service.dto.request.CancelEventRequest;
import event_service.dto.request.CreateEventRequest;
import event_service.dto.request.RejectParticipantRequest;
import event_service.dto.request.UpdateEventRequest;
import event_service.entity.Event;
import event_service.entity.EventFavorite;
import event_service.entity.EventParticipant;
import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import event_service.enums.EventType;
import event_service.enums.EventVisibility;
import event_service.enums.ParticipantStatus;
import event_service.exception.EventAlreadyJoinedException;
import event_service.exception.EventPermissionDeniedException;
import event_service.exception.FavoriteAlreadyExistsException;
import event_service.mapper.EventMapper;
import event_service.publisher.EventPublisher;
import event_service.repository.EventAuditRepository;
import event_service.repository.EventFavoriteRepository;
import event_service.repository.EventMediaRepository;
import event_service.repository.EventParticipantRepository;
import event_service.repository.EventRepository;
import event_service.security.UserContext;
import event_service.security.UserContextHolder;
import event_service.validator.EventValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class EventServiceImplTest {

    private EventRepository eventRepository;
    private EventParticipantRepository participantRepository;
    private EventFavoriteRepository favoriteRepository;
    private EventMediaRepository mediaRepository;
    private EventAuditRepository auditRepository;
    private MediaServiceClient mediaServiceClient;
    private LoggingClient loggingClient;
    private EventPublisher eventPublisher;
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        participantRepository = Mockito.mock(EventParticipantRepository.class);
        favoriteRepository = Mockito.mock(EventFavoriteRepository.class);
        mediaRepository = Mockito.mock(EventMediaRepository.class);
        auditRepository = Mockito.mock(EventAuditRepository.class);
        mediaServiceClient = Mockito.mock(MediaServiceClient.class);
        loggingClient = Mockito.mock(LoggingClient.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        eventService = new EventServiceImpl(
            eventRepository,
            participantRepository,
            favoriteRepository,
            mediaRepository,
            auditRepository,
            new EventMapper(),
            new EventValidator(),
            mediaServiceClient,
            loggingClient,
            eventPublisher
        );
    }

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void shouldValidateAlreadyJoined() {
        Event event = baseEvent();
        Mockito.when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        Mockito.when(participantRepository.existsByEventIdAndUserIdAndStatusIn(
            Mockito.eq(1L), Mockito.eq(10L), Mockito.anyList())).thenReturn(true);

        UserContext context = UserContext.builder().userId(10L).username("user").fullName("User").build();
        Assertions.assertThrows(EventAlreadyJoinedException.class,
            () -> eventService.joinEvent(1L, context));
    }

    @Test
    void shouldValidatePermission() {
        Event event = baseEvent();
        event.setOrganizerId(99L);
        Mockito.when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event));

        UserContextHolder.set(UserContext.builder().userId(10L).build());

        UpdateEventRequest request = baseUpdateRequest();
        Assertions.assertThrows(EventPermissionDeniedException.class,
            () -> eventService.updateEvent(1L, request, 10L));
    }

    @Test
    void shouldValidateFavoriteDuplicate() {
        Event event = baseEvent();
        Mockito.when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        Mockito.when(favoriteRepository.existsByEventIdAndUserId(1L, 10L)).thenReturn(true);

        Assertions.assertThrows(FavoriteAlreadyExistsException.class,
            () -> eventService.favoriteEvent(1L, 10L));
    }

    @Test
    void shouldCreateEvent() {
        CreateEventRequest request = baseCreateRequest();
        UserContext context = UserContext.builder().userId(10L).username("user").fullName("User").build();
        UserContextHolder.set(context);

        Mockito.when(mediaServiceClient.validateMediaIds(Mockito.any()))
            .thenReturn(ValidateMediaResponse.builder().valid(true).build());
        Mockito.when(mediaServiceClient.getMediaSummaries(Mockito.any()))
            .thenReturn(List.of(MediaSummaryResponse.builder().mediaId(1L).mediaUrl("url").mediaType("IMAGE").build()));
        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0, Event.class);
            event.setId(1L);
            return event;
        });

        Assertions.assertNotNull(eventService.createEvent(request, context));
    }

    @Test
    void shouldUpdateEvent() {
        Event event = baseEvent();
        event.setOrganizerId(10L);
        Mockito.when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        Mockito.when(mediaServiceClient.validateMediaIds(Mockito.any()))
            .thenReturn(ValidateMediaResponse.builder().valid(true).build());
        Mockito.when(mediaServiceClient.getMediaSummaries(Mockito.any())).thenReturn(List.of());

        UserContextHolder.set(UserContext.builder().userId(10L).build());

        UpdateEventRequest request = baseUpdateRequest();
        Assertions.assertNotNull(eventService.updateEvent(1L, request, 10L));
    }

    @Test
    void shouldCancelEvent() {
        Event event = baseEvent();
        event.setOrganizerId(10L);
        Mockito.when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event));

        UserContextHolder.set(UserContext.builder().userId(10L).build());
        eventService.cancelEvent(1L, new CancelEventRequest("Reason"), 10L);

        Assertions.assertEquals(EventStatus.CANCELLED, event.getStatus());
    }

    @Test
    void shouldJoinAndLeaveEvent() {
        Event event = baseEvent();
        event.setRequiresApproval(false);
        Mockito.when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        Mockito.when(participantRepository.existsByEventIdAndUserIdAndStatusIn(
            Mockito.eq(1L), Mockito.eq(10L), Mockito.anyList())).thenReturn(false);

        UserContext context = UserContext.builder().userId(10L).username("user").fullName("User").build();
        EventParticipant saved = EventParticipant.builder().id(5L).eventId(1L).userId(10L)
            .status(ParticipantStatus.APPROVED).joinedAt(LocalDateTime.now()).build();
        Mockito.when(participantRepository.save(Mockito.any(EventParticipant.class))).thenReturn(saved);

        Assertions.assertNotNull(eventService.joinEvent(1L, context));

        Mockito.when(participantRepository.findByEventIdAndUserId(1L, 10L))
            .thenReturn(Optional.of(saved));

        eventService.leaveEvent(1L, 10L);
        Assertions.assertEquals(ParticipantStatus.LEFT, saved.getStatus());
    }

    @Test
    void shouldFavoriteAndUnfavorite() {
        Event event = baseEvent();
        Mockito.when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event));
        Mockito.when(favoriteRepository.existsByEventIdAndUserId(1L, 10L)).thenReturn(false);

        eventService.favoriteEvent(1L, 10L);

        Mockito.when(favoriteRepository.findByEventIdAndUserId(1L, 10L))
            .thenReturn(Optional.of(EventFavorite.builder().id(1L).eventId(1L).userId(10L).build()));

        eventService.unfavoriteEvent(1L, 10L);
    }

    @Test
    void shouldApproveAndRejectParticipant() {
        Event event = baseEvent();
        event.setOrganizerId(10L);
        Mockito.when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(event));

        EventParticipant participant = EventParticipant.builder()
            .id(2L).eventId(1L).userId(22L).status(ParticipantStatus.PENDING)
            .joinedAt(LocalDateTime.now()).build();
        Mockito.when(participantRepository.findById(2L)).thenReturn(Optional.of(participant));

        UserContextHolder.set(UserContext.builder().userId(10L).build());

        Assertions.assertNotNull(eventService.approveParticipant(1L, 2L, 10L));

        participant.setStatus(ParticipantStatus.PENDING);
        Assertions.assertNotNull(eventService.rejectParticipant(1L, 2L,
            new RejectParticipantRequest(2L, "No"), 10L));
    }

    private Event baseEvent() {
        return Event.builder()
            .id(1L)
            .title("Title")
            .description("Desc")
            .category(EventCategory.ACADEMIC)
            .type(EventType.PHYSICAL)
            .visibility(EventVisibility.PUBLIC)
            .status(EventStatus.PUBLISHED)
            .organizerId(10L)
            .organizerName("Org")
            .requiresApproval(false)
            .startDateTime(LocalDateTime.now().plusDays(1))
            .endDateTime(LocalDateTime.now().plusDays(2))
            .participantCount(0)
            .favoriteCount(0)
            .viewCount(0L)
            .isOnline(false)
            .isFeatured(false)
            .isCancelled(false)
            .build();
    }

    private CreateEventRequest baseCreateRequest() {
        return CreateEventRequest.builder()
            .title("Title")
            .description("Desc")
            .shortDescription("Short")
            .category(EventCategory.ACADEMIC)
            .type(EventType.PHYSICAL)
            .visibility(EventVisibility.PUBLIC)
            .startDateTime(LocalDateTime.now().plusDays(2))
            .endDateTime(LocalDateTime.now().plusDays(3))
            .requiresApproval(false)
            .mediaIds(List.of(1L))
            .build();
    }

    private UpdateEventRequest baseUpdateRequest() {
        return UpdateEventRequest.builder()
            .title("Title")
            .description("Desc")
            .shortDescription("Short")
            .category(EventCategory.ACADEMIC)
            .type(EventType.PHYSICAL)
            .visibility(EventVisibility.PUBLIC)
            .startDateTime(LocalDateTime.now().plusDays(2))
            .endDateTime(LocalDateTime.now().plusDays(3))
            .requiresApproval(false)
            .build();
    }
}
