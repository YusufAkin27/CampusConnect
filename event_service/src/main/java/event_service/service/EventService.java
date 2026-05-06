package event_service.service;

import event_service.dto.request.CancelEventRequest;
import event_service.dto.request.CreateEventRequest;
import event_service.dto.request.RejectParticipantRequest;
import event_service.dto.request.SearchEventRequest;
import event_service.dto.request.UpdateEventRequest;
import event_service.dto.response.EventParticipantResponse;
import event_service.dto.response.EventResponse;
import event_service.dto.response.EventSummaryResponse;
import event_service.enums.EventCategory;
import event_service.security.UserContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {

    EventResponse createEvent(CreateEventRequest request, UserContext userContext);

    EventResponse updateEvent(Long eventId, UpdateEventRequest request, Long userId);

    void cancelEvent(Long eventId, CancelEventRequest request, Long userId);

    EventResponse getEventById(Long eventId);

    Page<EventSummaryResponse> getEvents(Pageable pageable);

    Page<EventSummaryResponse> searchEvents(SearchEventRequest request);

    Page<EventSummaryResponse> getUpcomingEvents(Pageable pageable);

    Page<EventSummaryResponse> getPopularEvents(Pageable pageable);

    Page<EventSummaryResponse> getEventsByCategory(EventCategory category, Pageable pageable);

    EventParticipantResponse joinEvent(Long eventId, UserContext userContext);

    void leaveEvent(Long eventId, Long userId);

    Page<EventParticipantResponse> getParticipants(Long eventId, Pageable pageable);

    EventParticipantResponse approveParticipant(Long eventId, Long participantId, Long userId);

    EventParticipantResponse rejectParticipant(Long eventId, Long participantId, RejectParticipantRequest request, Long userId);

    void favoriteEvent(Long eventId, Long userId);

    void unfavoriteEvent(Long eventId, Long userId);

    Page<EventSummaryResponse> getMyFavoriteEvents(Long userId, Pageable pageable);

    Page<EventSummaryResponse> getMyJoinedEvents(Long userId, Pageable pageable);

    Page<EventSummaryResponse> getMyCreatedEvents(Long userId, Pageable pageable);

    EventResponse publishEvent(Long eventId, Long userId);

    EventResponse featureEvent(Long eventId, Long userId);

    EventResponse unfeatureEvent(Long eventId, Long userId);

    EventResponse completeEvent(Long eventId, Long userId);
}
