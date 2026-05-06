package event_service.mapper;

import event_service.dto.request.CreateEventRequest;
import event_service.dto.request.UpdateEventRequest;
import event_service.dto.response.EventMediaResponse;
import event_service.dto.response.EventParticipantResponse;
import event_service.dto.response.EventResponse;
import event_service.dto.response.EventSummaryResponse;
import event_service.entity.Event;
import event_service.entity.EventMedia;
import event_service.entity.EventParticipant;
import event_service.enums.EventStatus;
import event_service.enums.EventType;
import event_service.enums.OrganizerType;
import event_service.security.UserContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(CreateEventRequest request, UserContext userContext, EventStatus status, OrganizerType organizerType) {
        boolean isOnline = request.getType() == EventType.ONLINE || request.getType() == EventType.HYBRID;
        return Event.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .shortDescription(request.getShortDescription())
            .category(request.getCategory())
            .type(request.getType())
            .status(status)
            .visibility(request.getVisibility())
            .organizerId(userContext.getUserId())
            .organizerName(Optional.ofNullable(userContext.getFullName()).orElse(userContext.getUsername()))
            .organizerType(organizerType)
            .campusName(request.getCampusName())
            .faculty(request.getFaculty())
            .department(request.getDepartment())
            .locationName(request.getLocationName())
            .locationAddress(request.getLocationAddress())
            .onlineUrl(request.getOnlineUrl())
            .isOnline(isOnline)
            .startDateTime(request.getStartDateTime())
            .endDateTime(request.getEndDateTime())
            .registrationStartDateTime(request.getRegistrationStartDateTime())
            .registrationEndDateTime(request.getRegistrationEndDateTime())
            .capacity(request.getCapacity())
            .participantCount(0)
            .favoriteCount(0)
            .viewCount(0L)
            .requiresApproval(Boolean.TRUE.equals(request.getRequiresApproval()))
            .isFeatured(false)
            .isCancelled(false)
            .cancellationReason(null)
            .build();
    }

    public void updateEntity(Event event, UpdateEventRequest request) {
        boolean isOnline = request.getType() == EventType.ONLINE || request.getType() == EventType.HYBRID;
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setShortDescription(request.getShortDescription());
        event.setCategory(request.getCategory());
        event.setType(request.getType());
        event.setVisibility(request.getVisibility());
        event.setCampusName(request.getCampusName());
        event.setFaculty(request.getFaculty());
        event.setDepartment(request.getDepartment());
        event.setLocationName(request.getLocationName());
        event.setLocationAddress(request.getLocationAddress());
        event.setOnlineUrl(request.getOnlineUrl());
        event.setOnline(isOnline);
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setRegistrationStartDateTime(request.getRegistrationStartDateTime());
        event.setRegistrationEndDateTime(request.getRegistrationEndDateTime());
        event.setCapacity(request.getCapacity());
        event.setRequiresApproval(Boolean.TRUE.equals(request.getRequiresApproval()));
    }

    public EventResponse toResponse(Event event, List<EventMediaResponse> media) {
        return EventResponse.builder()
            .id(event.getId())
            .title(event.getTitle())
            .description(event.getDescription())
            .shortDescription(event.getShortDescription())
            .category(event.getCategory())
            .type(event.getType())
            .status(event.getStatus())
            .visibility(event.getVisibility())
            .organizerId(event.getOrganizerId())
            .organizerName(event.getOrganizerName())
            .organizerType(event.getOrganizerType())
            .campusName(event.getCampusName())
            .faculty(event.getFaculty())
            .department(event.getDepartment())
            .locationName(event.getLocationName())
            .locationAddress(event.getLocationAddress())
            .onlineUrl(event.getOnlineUrl())
            .isOnline(event.isOnline())
            .startDateTime(event.getStartDateTime())
            .endDateTime(event.getEndDateTime())
            .registrationStartDateTime(event.getRegistrationStartDateTime())
            .registrationEndDateTime(event.getRegistrationEndDateTime())
            .capacity(event.getCapacity())
            .participantCount(event.getParticipantCount())
            .favoriteCount(event.getFavoriteCount())
            .viewCount(event.getViewCount())
            .requiresApproval(event.isRequiresApproval())
            .isFeatured(event.isFeatured())
            .isCancelled(event.isCancelled())
            .cancellationReason(event.getCancellationReason())
            .mediaList(media)
            .createdAt(event.getCreatedAt())
            .updatedAt(event.getUpdatedAt())
            .build();
    }

    public EventSummaryResponse toSummaryResponse(Event event, String coverMediaUrl) {
        return EventSummaryResponse.builder()
            .id(event.getId())
            .title(event.getTitle())
            .shortDescription(event.getShortDescription())
            .category(event.getCategory())
            .type(event.getType())
            .status(event.getStatus())
            .locationName(event.getLocationName())
            .isOnline(event.isOnline())
            .startDateTime(event.getStartDateTime())
            .endDateTime(event.getEndDateTime())
            .capacity(event.getCapacity())
            .participantCount(event.getParticipantCount())
            .favoriteCount(event.getFavoriteCount())
            .coverMediaUrl(coverMediaUrl)
            .build();
    }

    public EventParticipantResponse toParticipantResponse(EventParticipant participant) {
        return EventParticipantResponse.builder()
            .id(participant.getId())
            .eventId(participant.getEventId())
            .userId(participant.getUserId())
            .username(participant.getUsername())
            .fullName(participant.getFullName())
            .status(participant.getStatus())
            .joinedAt(participant.getJoinedAt())
            .approvedAt(participant.getApprovedAt())
            .rejectedAt(participant.getRejectedAt())
            .build();
    }

    public EventMediaResponse toMediaResponse(EventMedia media) {
        return EventMediaResponse.builder()
            .mediaId(media.getMediaId())
            .mediaUrl(media.getMediaUrl())
            .mediaType(media.getMediaType())
            .orderIndex(media.getOrderIndex())
            .build();
    }
}
