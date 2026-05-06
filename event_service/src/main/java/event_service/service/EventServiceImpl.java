package event_service.service;

import event_service.client.LoggingClient;
import event_service.client.MediaServiceClient;
import event_service.client.dto.LogEventRequest;
import event_service.client.dto.MediaSummaryRequest;
import event_service.client.dto.MediaSummaryResponse;
import event_service.client.dto.MediaUsageRequest;
import event_service.client.dto.ValidateMediaRequest;
import event_service.client.dto.ValidateMediaResponse;
import event_service.dto.request.CancelEventRequest;
import event_service.dto.request.CreateEventRequest;
import event_service.dto.request.RejectParticipantRequest;
import event_service.dto.request.SearchEventRequest;
import event_service.dto.request.UpdateEventRequest;
import event_service.dto.response.EventMediaResponse;
import event_service.dto.response.EventParticipantResponse;
import event_service.dto.response.EventResponse;
import event_service.dto.response.EventSummaryResponse;
import event_service.entity.Event;
import event_service.entity.EventAudit;
import event_service.entity.EventFavorite;
import event_service.entity.EventMedia;
import event_service.entity.EventParticipant;
import event_service.enums.EventAuditAction;
import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import event_service.enums.OrganizerType;
import event_service.enums.ParticipantStatus;
import event_service.exception.EventAlreadyCancelledException;
import event_service.exception.EventAlreadyJoinedException;
import event_service.exception.EventNotFoundException;
import event_service.exception.EventNotJoinedException;
import event_service.exception.EventPermissionDeniedException;
import event_service.exception.FavoriteAlreadyExistsException;
import event_service.exception.FavoriteNotFoundException;
import event_service.exception.MediaValidationFailedException;
import event_service.exception.ParticipantAlreadyApprovedException;
import event_service.exception.ParticipantAlreadyRejectedException;
import event_service.exception.ParticipantNotFoundException;
import event_service.mapper.EventMapper;
import event_service.publisher.EventPublisher;
import event_service.repository.EventAuditRepository;
import event_service.repository.EventFavoriteRepository;
import event_service.repository.EventMediaRepository;
import event_service.repository.EventParticipantRepository;
import event_service.repository.EventRepository;
import event_service.repository.EventSpecifications;
import event_service.security.UserContext;
import event_service.security.UserContextHolder;
import event_service.validator.EventValidator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final String MEDIA_USAGE_TYPE = "EVENT";

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventFavoriteRepository eventFavoriteRepository;
    private final EventMediaRepository eventMediaRepository;
    private final EventAuditRepository eventAuditRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final MediaServiceClient mediaServiceClient;
    private final LoggingClient loggingClient;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request, UserContext userContext) {
        eventValidator.validateEventDates(request.getStartDateTime(), request.getEndDateTime());
        eventValidator.validateRegistrationDates(
            request.getRegistrationStartDateTime(),
            request.getRegistrationEndDateTime(),
            request.getStartDateTime()
        );

        OrganizerType organizerType = resolveOrganizerType(userContext);
        EventStatus initialStatus = userContext.isAdmin() ? EventStatus.PUBLISHED : EventStatus.PENDING_APPROVAL;

        List<MediaSummaryResponse> mediaSummaries = validateAndFetchMedia(request.getMediaIds());

        Event event = eventMapper.toEntity(request, userContext, initialStatus, organizerType);
        Event saved = eventRepository.save(event);

        saveEventMedia(saved.getId(), mediaSummaries);
        registerMediaUsage(mediaSummaries, saved.getId());

        recordAudit(saved.getId(), userContext.getUserId(), EventAuditAction.CREATED, null, saved.getStatus().name(),
            "Event created");
        safeLog("EVENT_CREATED", userContext.getUserId(), saved.getId(), "Event created");
        eventPublisher.publishEventCreated(saved);

        return toEventResponse(saved);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long eventId, UpdateEventRequest request, Long userId) {
        Event event = getEventForUpdate(eventId);
        validateOrganizerOrAdmin(event, userId);
        eventValidator.validateUpdatable(event);
        eventValidator.validateEventDates(request.getStartDateTime(), request.getEndDateTime());
        eventValidator.validateRegistrationDates(
            request.getRegistrationStartDateTime(),
            request.getRegistrationEndDateTime(),
            request.getStartDateTime()
        );

        List<MediaSummaryResponse> mediaSummaries = validateAndFetchMedia(request.getMediaIds());

        eventMapper.updateEntity(event, request);
        Event saved = eventRepository.save(event);

        updateEventMedia(saved.getId(), mediaSummaries);

        recordAudit(saved.getId(), userId, EventAuditAction.UPDATED, null, saved.getStatus().name(), "Event updated");
        safeLog("EVENT_UPDATED", userId, saved.getId(), "Event updated");
        eventPublisher.publishEventUpdated(saved);

        return toEventResponse(saved);
    }

    @Override
    @Transactional
    public void cancelEvent(Long eventId, CancelEventRequest request, Long userId) {
        Event event = getEventForUpdate(eventId);
        validateOrganizerOrAdmin(event, userId);
        eventValidator.validateCancelable(event);
        event.setStatus(EventStatus.CANCELLED);
        event.setCancelled(true);
        event.setCancellationReason(request.getReason());
        eventRepository.save(event);

        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId, Pageable.unpaged())
            .getContent();
        for (EventParticipant participant : participants) {
            participant.setStatus(ParticipantStatus.CANCELLED);
        }
        eventParticipantRepository.saveAll(participants);

        recordAudit(eventId, userId, EventAuditAction.CANCELLED, null, EventStatus.CANCELLED.name(),
            "Event cancelled");
        safeLog("EVENT_CANCELLED", userId, eventId, "Event cancelled");
        eventPublisher.publishEventCancelled(event);
    }

    @Override
    @Transactional
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found"));
        event.setViewCount(event.getViewCount() + 1);
        eventRepository.save(event);
        return toEventResponse(event);
    }

    @Override
    public Page<EventSummaryResponse> getEvents(Pageable pageable) {
        return mapToSummary(eventRepository.findAll(pageable));
    }

    @Override
    public Page<EventSummaryResponse> searchEvents(SearchEventRequest request) {
        Pageable pageable = buildSearchPageable(request);
        Specification<Event> specification = Specification.where(EventSpecifications.keywordContains(request.getKeyword()))
            .and(EventSpecifications.hasCategory(request.getCategory()))
            .and(EventSpecifications.hasType(request.getType()))
            .and(EventSpecifications.hasStatus(request.getStatus()))
            .and(EventSpecifications.campusEquals(request.getCampusName()))
            .and(EventSpecifications.facultyEquals(request.getFaculty()))
            .and(EventSpecifications.departmentEquals(request.getDepartment()))
            .and(EventSpecifications.startDateFrom(request.getStartDate()))
            .and(EventSpecifications.endDateTo(request.getEndDate()))
            .and(EventSpecifications.onlyOnline(request.getOnlyOnline()))
            .and(EventSpecifications.onlyFeatured(request.getOnlyFeatured()));
        return mapToSummary(eventRepository.findAll(specification, pageable));
    }

    @Override
    public Page<EventSummaryResponse> getUpcomingEvents(Pageable pageable) {
        return mapToSummary(eventRepository.findByStartDateTimeAfter(LocalDateTime.now(), pageable));
    }

    @Override
    public Page<EventSummaryResponse> getPopularEvents(Pageable pageable) {
        return mapToSummary(eventRepository.findPopularEvents(pageable));
    }

    @Override
    public Page<EventSummaryResponse> getEventsByCategory(EventCategory category, Pageable pageable) {
        return mapToSummary(eventRepository.findByCategory(category, pageable));
    }

    @Override
    @Transactional
    public EventParticipantResponse joinEvent(Long eventId, UserContext userContext) {
        Event event = getEventForUpdate(eventId);
        eventValidator.validateJoinable(event);

        boolean alreadyJoined = eventParticipantRepository.existsByEventIdAndUserIdAndStatusIn(
            eventId,
            userContext.getUserId(),
            List.of(ParticipantStatus.PENDING, ParticipantStatus.APPROVED)
        );
        if (alreadyJoined) {
            throw new EventAlreadyJoinedException("User already joined the event");
        }

        ParticipantStatus status = event.isRequiresApproval() ? ParticipantStatus.PENDING : ParticipantStatus.APPROVED;
        EventParticipant participant = EventParticipant.builder()
            .eventId(eventId)
            .userId(userContext.getUserId())
            .username(userContext.getUsername())
            .fullName(userContext.getFullName())
            .status(status)
            .joinedAt(LocalDateTime.now())
            .approvedAt(status == ParticipantStatus.APPROVED ? LocalDateTime.now() : null)
            .build();
        EventParticipant savedParticipant = eventParticipantRepository.save(participant);

        if (status == ParticipantStatus.APPROVED) {
            event.setParticipantCount(event.getParticipantCount() + 1);
            eventRepository.save(event);
        }

        recordAudit(eventId, userContext.getUserId(), EventAuditAction.PARTICIPANT_JOINED, null, null,
            "Participant joined");
        safeLog("EVENT_JOINED", userContext.getUserId(), eventId, "Event joined");
        eventPublisher.publishEventJoined(event, userContext.getUserId());

        return eventMapper.toParticipantResponse(savedParticipant);
    }

    @Override
    @Transactional
    public void leaveEvent(Long eventId, Long userId) {
        Event event = getEventForUpdate(eventId);
        EventParticipant participant = eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
            .orElseThrow(() -> new EventNotJoinedException("User not joined to event"));

        if (event.getStartDateTime() != null && !LocalDateTime.now().isBefore(event.getStartDateTime())) {
            throw new event_service.exception.EventAlreadyStartedException("Event already started");
        }

        if (participant.getStatus() == ParticipantStatus.APPROVED) {
            event.setParticipantCount(Math.max(0, event.getParticipantCount() - 1));
            eventRepository.save(event);
        }

        participant.setStatus(ParticipantStatus.LEFT);
        participant.setCancelReason("User left");
        eventParticipantRepository.save(participant);

        recordAudit(eventId, userId, EventAuditAction.PARTICIPANT_LEFT, null, null, "Participant left");
        safeLog("EVENT_LEFT", userId, eventId, "Event left");
    }

    @Override
    public Page<EventParticipantResponse> getParticipants(Long eventId, Pageable pageable) {
        Page<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId, pageable);
        List<EventParticipantResponse> responses = participants.getContent().stream()
            .map(eventMapper::toParticipantResponse)
            .toList();
        return new PageImpl<>(responses, pageable, participants.getTotalElements());
    }

    @Override
    @Transactional
    public EventParticipantResponse approveParticipant(Long eventId, Long participantId, Long userId) {
        Event event = getEventForUpdate(eventId);
        validateOrganizerOrAdmin(event, userId);

        EventParticipant participant = eventParticipantRepository.findById(participantId)
            .orElseThrow(() -> new ParticipantNotFoundException("Participant not found"));
        if (!participant.getEventId().equals(eventId)) {
            throw new ParticipantNotFoundException("Participant not found for event");
        }
        if (participant.getStatus() == ParticipantStatus.APPROVED) {
            throw new ParticipantAlreadyApprovedException("Participant already approved");
        }
        if (participant.getStatus() == ParticipantStatus.REJECTED) {
            throw new ParticipantAlreadyRejectedException("Participant already rejected");
        }

        eventValidator.validateCapacity(event);

        participant.setStatus(ParticipantStatus.APPROVED);
        participant.setApprovedAt(LocalDateTime.now());
        eventParticipantRepository.save(participant);

        event.setParticipantCount(event.getParticipantCount() + 1);
        eventRepository.save(event);

        recordAudit(eventId, userId, EventAuditAction.PARTICIPANT_APPROVED, null, null, "Participant approved");
        safeLog("PARTICIPANT_APPROVED", userId, eventId, "Participant approved");

        return eventMapper.toParticipantResponse(participant);
    }

    @Override
    @Transactional
    public EventParticipantResponse rejectParticipant(Long eventId, Long participantId,
                                                     RejectParticipantRequest request, Long userId) {
        Event event = getEventForUpdate(eventId);
        validateOrganizerOrAdmin(event, userId);

        EventParticipant participant = eventParticipantRepository.findById(participantId)
            .orElseThrow(() -> new ParticipantNotFoundException("Participant not found"));
        if (!participant.getEventId().equals(eventId)) {
            throw new ParticipantNotFoundException("Participant not found for event");
        }
        if (participant.getStatus() == ParticipantStatus.APPROVED) {
            throw new ParticipantAlreadyApprovedException("Participant already approved");
        }
        if (participant.getStatus() == ParticipantStatus.REJECTED) {
            throw new ParticipantAlreadyRejectedException("Participant already rejected");
        }

        participant.setStatus(ParticipantStatus.REJECTED);
        participant.setRejectedAt(LocalDateTime.now());
        participant.setCancelReason(request.getReason());
        eventParticipantRepository.save(participant);

        recordAudit(eventId, userId, EventAuditAction.PARTICIPANT_REJECTED, null, null, "Participant rejected");
        safeLog("PARTICIPANT_REJECTED", userId, eventId, "Participant rejected");

        return eventMapper.toParticipantResponse(participant);
    }

    @Override
    @Transactional
    public void favoriteEvent(Long eventId, Long userId) {
        Event event = getEventForUpdate(eventId);
        if (eventFavoriteRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new FavoriteAlreadyExistsException("Event already favorited");
        }

        EventFavorite favorite = EventFavorite.builder()
            .eventId(eventId)
            .userId(userId)
            .createdAt(LocalDateTime.now())
            .build();
        eventFavoriteRepository.save(favorite);

        event.setFavoriteCount(event.getFavoriteCount() + 1);
        eventRepository.save(event);

        recordAudit(eventId, userId, EventAuditAction.FAVORITED, null, null, "Event favorited");
        safeLog("EVENT_FAVORITED", userId, eventId, "Event favorited");
    }

    @Override
    @Transactional
    public void unfavoriteEvent(Long eventId, Long userId) {
        Event event = getEventForUpdate(eventId);
        Optional<EventFavorite> favorite = eventFavoriteRepository.findByEventIdAndUserId(eventId, userId);
        if (favorite.isEmpty()) {
            throw new FavoriteNotFoundException("Favorite not found");
        }
        eventFavoriteRepository.deleteByEventIdAndUserId(eventId, userId);

        event.setFavoriteCount(Math.max(0, event.getFavoriteCount() - 1));
        eventRepository.save(event);

        recordAudit(eventId, userId, EventAuditAction.UNFAVORITED, null, null, "Event unfavorited");
        safeLog("EVENT_UNFAVORITED", userId, eventId, "Event unfavorited");
    }

    @Override
    public Page<EventSummaryResponse> getMyFavoriteEvents(Long userId, Pageable pageable) {
        Page<EventFavorite> favorites = eventFavoriteRepository.findByUserId(userId, pageable);
        List<Long> eventIds = favorites.getContent().stream().map(EventFavorite::getEventId).toList();
        return mapEventsByIds(eventIds, pageable, favorites.getTotalElements());
    }

    @Override
    public Page<EventSummaryResponse> getMyJoinedEvents(Long userId, Pageable pageable) {
        Page<EventParticipant> participants = eventParticipantRepository.findByUserId(userId, pageable);
        List<Long> eventIds = participants.getContent().stream().map(EventParticipant::getEventId).toList();
        return mapEventsByIds(eventIds, pageable, participants.getTotalElements());
    }

    @Override
    public Page<EventSummaryResponse> getMyCreatedEvents(Long userId, Pageable pageable) {
        return mapToSummary(eventRepository.findByOrganizerId(userId, pageable));
    }

    @Override
    @Transactional
    public EventResponse publishEvent(Long eventId, Long userId) {
        Event event = getEventForUpdate(eventId);
        validateOrganizerOrAdmin(event, userId);
        eventValidator.validatePublishable(event);
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new EventAlreadyCancelledException("Event already cancelled");
        }
        event.setStatus(EventStatus.PUBLISHED);
        Event saved = eventRepository.save(event);

        recordAudit(eventId, userId, EventAuditAction.PUBLISHED, null, EventStatus.PUBLISHED.name(), "Event published");
        safeLog("EVENT_PUBLISHED", userId, eventId, "Event published");

        return toEventResponse(saved);
    }

    @Override
    @Transactional
    public EventResponse featureEvent(Long eventId, Long userId) {
        Event event = getEventForUpdate(eventId);
        validateOrganizerOrAdmin(event, userId);
        event.setFeatured(true);
        Event saved = eventRepository.save(event);
        return toEventResponse(saved);
    }

    @Override
    @Transactional
    public EventResponse unfeatureEvent(Long eventId, Long userId) {
        Event event = getEventForUpdate(eventId);
        validateOrganizerOrAdmin(event, userId);
        event.setFeatured(false);
        Event saved = eventRepository.save(event);
        return toEventResponse(saved);
    }

    @Override
    @Transactional
    public EventResponse completeEvent(Long eventId, Long userId) {
        Event event = getEventForUpdate(eventId);
        validateOrganizerOrAdmin(event, userId);
        event.setStatus(EventStatus.COMPLETED);
        Event saved = eventRepository.save(event);

        recordAudit(eventId, userId, EventAuditAction.COMPLETED, null, EventStatus.COMPLETED.name(),
            "Event completed");
        safeLog("EVENT_COMPLETED", userId, eventId, "Event completed");

        return toEventResponse(saved);
    }

    private OrganizerType resolveOrganizerType(UserContext userContext) {
        if (userContext.isAdmin()) {
            return OrganizerType.ADMIN;
        }
        return OrganizerType.STUDENT;
    }

    private Event getEventForUpdate(Long eventId) {
        return eventRepository.findByIdForUpdate(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    private void validateOrganizerOrAdmin(Event event, Long userId) {
        UserContext context = UserContextHolder.get();
        boolean isAdmin = context != null && context.isAdmin();
        if (!isAdmin && !event.getOrganizerId().equals(userId)) {
            throw new EventPermissionDeniedException("Permission denied");
        }
    }

    private EventResponse toEventResponse(Event event) {
        List<EventMedia> media = eventMediaRepository.findByEventIdOrderByOrderIndexAsc(event.getId());
        List<EventMediaResponse> mediaResponses = media.stream()
            .map(eventMapper::toMediaResponse)
            .toList();
        return eventMapper.toResponse(event, mediaResponses);
    }

    private Page<EventSummaryResponse> mapToSummary(Page<Event> events) {
        List<EventSummaryResponse> responses = events.getContent().stream()
            .map(event -> eventMapper.toSummaryResponse(event, getCoverMediaUrl(event.getId())))
            .toList();
        return new PageImpl<>(responses, events.getPageable(), events.getTotalElements());
    }

    private Page<EventSummaryResponse> mapEventsByIds(List<Long> eventIds, Pageable pageable, long totalElements) {
        if (eventIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalElements);
        }
        Map<Long, Event> eventMap = eventRepository.findAllById(eventIds).stream()
            .collect(Collectors.toMap(Event::getId, event -> event));
        List<EventSummaryResponse> responses = eventIds.stream()
            .map(eventMap::get)
            .filter(event -> event != null)
            .map(event -> eventMapper.toSummaryResponse(event, getCoverMediaUrl(event.getId())))
            .toList();
        return new PageImpl<>(responses, pageable, totalElements);
    }

    private String getCoverMediaUrl(Long eventId) {
        List<EventMedia> media = eventMediaRepository.findByEventIdOrderByOrderIndexAsc(eventId);
        if (media.isEmpty()) {
            return null;
        }
        return media.get(0).getMediaUrl();
    }

    private Pageable buildSearchPageable(SearchEventRequest request) {
        int page = request.getPage() == null ? 0 : request.getPage();
        int size = request.getSize() == null ? 20 : request.getSize();
        Sort sort = Sort.by("startDateTime").ascending();
        if (request.getSortBy() != null && request.getSortDirection() != null) {
            Sort.Direction direction = Sort.Direction.fromOptionalString(request.getSortDirection())
                .orElse(Sort.Direction.ASC);
            sort = Sort.by(direction, request.getSortBy());
        }
        return PageRequest.of(page, size, sort);
    }

    private List<MediaSummaryResponse> validateAndFetchMedia(List<Long> mediaIds) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            return Collections.emptyList();
        }
        ValidateMediaResponse validation = mediaServiceClient.validateMediaIds(
            ValidateMediaRequest.builder().mediaIds(mediaIds).build());
        if (validation == null || !validation.isValid()) {
            throw new MediaValidationFailedException("Media validation failed");
        }
        return mediaServiceClient.getMediaSummaries(MediaSummaryRequest.builder().mediaIds(mediaIds).build());
    }

    private void saveEventMedia(Long eventId, List<MediaSummaryResponse> mediaSummaries) {
        if (mediaSummaries.isEmpty()) {
            return;
        }
        List<EventMedia> media = new ArrayList<>();
        int index = 0;
        for (MediaSummaryResponse summary : mediaSummaries) {
            media.add(EventMedia.builder()
                .eventId(eventId)
                .mediaId(summary.getMediaId())
                .mediaUrl(summary.getMediaUrl())
                .mediaType(summary.getMediaType())
                .orderIndex(index++)
                .createdAt(LocalDateTime.now())
                .build());
        }
        eventMediaRepository.saveAll(media);
    }

    private void updateEventMedia(Long eventId, List<MediaSummaryResponse> mediaSummaries) {
        List<EventMedia> existing = eventMediaRepository.findByEventId(eventId);
        Set<Long> existingIds = existing.stream().map(EventMedia::getMediaId).collect(Collectors.toSet());
        Set<Long> newIds = mediaSummaries.stream().map(MediaSummaryResponse::getMediaId).collect(Collectors.toSet());

        List<Long> removed = existingIds.stream().filter(id -> !newIds.contains(id)).toList();
        List<Long> added = newIds.stream().filter(id -> !existingIds.contains(id)).toList();

        if (!removed.isEmpty()) {
            safeUnregisterMedia(removed, eventId);
        }
        if (!added.isEmpty()) {
            safeRegisterMedia(added, eventId);
        }

        eventMediaRepository.deleteByEventId(eventId);
        saveEventMedia(eventId, mediaSummaries);

        if (!added.isEmpty()) {
            recordAudit(eventId, null, EventAuditAction.MEDIA_ATTACHED, null, null, "Media attached");
            safeLog("EVENT_MEDIA_ATTACHED", null, eventId, "Media attached");
        }
        if (!removed.isEmpty()) {
            recordAudit(eventId, null, EventAuditAction.MEDIA_REMOVED, null, null, "Media removed");
            safeLog("EVENT_MEDIA_REMOVED", null, eventId, "Media removed");
        }
    }

    private void registerMediaUsage(List<MediaSummaryResponse> summaries, Long eventId) {
        List<Long> mediaIds = summaries.stream().map(MediaSummaryResponse::getMediaId).toList();
        safeRegisterMedia(mediaIds, eventId);
    }

    private void safeRegisterMedia(List<Long> mediaIds, Long eventId) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            return;
        }
        try {
            mediaServiceClient.registerMediaUsage(MediaUsageRequest.builder()
                .mediaIds(mediaIds)
                .usageType(MEDIA_USAGE_TYPE)
                .referenceId(eventId)
                .build());
        } catch (Exception ex) {
            log.warn("Media usage register failed", ex);
        }
    }

    private void safeUnregisterMedia(List<Long> mediaIds, Long eventId) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            return;
        }
        try {
            mediaServiceClient.unregisterMediaUsage(MediaUsageRequest.builder()
                .mediaIds(mediaIds)
                .usageType(MEDIA_USAGE_TYPE)
                .referenceId(eventId)
                .build());
        } catch (Exception ex) {
            log.warn("Media usage unregister failed", ex);
        }
    }

    private void recordAudit(Long eventId, Long actorUserId, EventAuditAction action, String oldStatus,
                             String newStatus, String description) {
        EventAudit audit = EventAudit.builder()
            .eventId(eventId)
            .actorUserId(actorUserId)
            .action(action)
            .oldStatus(oldStatus)
            .newStatus(newStatus)
            .description(description)
            .createdAt(LocalDateTime.now())
            .build();
        eventAuditRepository.save(audit);
    }

    private void safeLog(String eventType, Long userId, Long referenceId, String message) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("eventId", referenceId);
            LogEventRequest request = LogEventRequest.builder()
                .eventType(eventType)
                .actorUserId(userId)
                .referenceId(referenceId)
                .message(message)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
            loggingClient.logEvent(request);
        } catch (Exception ex) {
            log.warn("Logging service failed", ex);
        }
    }
}
