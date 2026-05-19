package event_service.controller;

import event_service.dto.request.CancelEventRequest;
import event_service.dto.request.CreateEventRequest;
import event_service.dto.request.RejectParticipantRequest;
import event_service.dto.request.SearchEventRequest;
import event_service.dto.request.UpdateEventRequest;
import event_service.dto.response.ApiResponse;
import event_service.dto.response.EventParticipantResponse;
import event_service.dto.response.EventResponse;
import event_service.dto.response.EventSummaryResponse;
import event_service.dto.response.PageResponse;
import event_service.enums.EventCategory;
import event_service.security.UserContext;
import event_service.security.UserContextHolder;
import event_service.security.UserContextResolver;
import event_service.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/events")
@Tag(name = "Event API", description = "Kampüs etkinlik yönetimi ve katılım işlemleri")
public class EventController {

    private final EventService eventService;
    private final UserContextResolver userContextResolver;

    @Operation(summary = "Yeni etkinlik oluşturur")
    @PostMapping
    public ApiResponse<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request,
                                                  HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventResponse response = eventService.createEvent(request, userContext);
            return ApiResponse.success("Event created", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinlik bilgilerini günceller")
    @PutMapping("/{eventId}")
    public ApiResponse<EventResponse> updateEvent(@PathVariable Long eventId,
                                                  @Valid @RequestBody UpdateEventRequest request,
                                                  HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventResponse response = eventService.updateEvent(eventId, request, userContext.getUserId());
            return ApiResponse.success("Event updated", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinliği iptal eder")
    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> cancelEvent(@PathVariable Long eventId,
                                         @Valid @RequestBody CancelEventRequest request,
                                         HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            eventService.cancelEvent(eventId, request, userContext.getUserId());
            return ApiResponse.success("Event cancelled", null);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinlik detaylarını getirir")
    @GetMapping("/{eventId}")
    public ApiResponse<EventResponse> getEvent(@PathVariable Long eventId) {
        return ApiResponse.success("Event fetched", eventService.getEventById(eventId));
    }

    @Operation(summary = "Tüm etkinlikleri listeler")
    @GetMapping
    public ApiResponse<PageResponse<EventSummaryResponse>> getEvents(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "20") int size,
                                                                     @RequestParam(defaultValue = "startDateTime") String sortBy,
                                                                     @RequestParam(defaultValue = "ASC") String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.ASC);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<EventSummaryResponse> result = eventService.getEvents(pageable);
        return ApiResponse.success("Events fetched", toPageResponse(result));
    }

    @Operation(summary = "Etkinliklerde arama yapar")
    @GetMapping("/search")
    public ApiResponse<PageResponse<EventSummaryResponse>> searchEvents(SearchEventRequest request) {
        Page<EventSummaryResponse> result = eventService.searchEvents(request);
        return ApiResponse.success("Events fetched", toPageResponse(result));
    }

    @Operation(summary = "Yaklaşan etkinlikleri listeler")
    @GetMapping("/upcoming")
    public ApiResponse<PageResponse<EventSummaryResponse>> upcomingEvents(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "20") int size) {
        Page<EventSummaryResponse> result = eventService.getUpcomingEvents(PageRequest.of(page, size));
        return ApiResponse.success("Upcoming events fetched", toPageResponse(result));
    }

    @Operation(summary = "Popüler etkinlikleri listeler")
    @GetMapping("/popular")
    public ApiResponse<PageResponse<EventSummaryResponse>> popularEvents(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "20") int size) {
        Page<EventSummaryResponse> result = eventService.getPopularEvents(PageRequest.of(page, size));
        return ApiResponse.success("Popular events fetched", toPageResponse(result));
    }

    @Operation(summary = "Kategoriye göre etkinlikleri listeler")
    @GetMapping("/category/{category}")
    public ApiResponse<PageResponse<EventSummaryResponse>> eventsByCategory(@PathVariable EventCategory category,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "20") int size) {
        Page<EventSummaryResponse> result = eventService.getEventsByCategory(category, PageRequest.of(page, size));
        return ApiResponse.success("Events fetched", toPageResponse(result));
    }

    @Operation(summary = "Kampüse göre etkinlikleri listeler")
    @GetMapping("/campus/{campusName}")
    public ApiResponse<PageResponse<EventSummaryResponse>> eventsByCampus(@PathVariable String campusName,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "20") int size) {
        SearchEventRequest request = SearchEventRequest.builder()
            .campusName(campusName)
            .page(page)
            .size(size)
            .build();
        Page<EventSummaryResponse> result = eventService.searchEvents(request);
        return ApiResponse.success("Events fetched", toPageResponse(result));
    }

    @Operation(summary = "Etkinliğe katılır")
    @PostMapping("/{eventId}/join")
    public ApiResponse<EventParticipantResponse> joinEvent(@PathVariable Long eventId,
                                                           HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventParticipantResponse response = eventService.joinEvent(eventId, userContext);
            return ApiResponse.success("Joined event", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinlikten ayrılır")
    @PostMapping("/{eventId}/leave")
    public ApiResponse<Void> leaveEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            eventService.leaveEvent(eventId, userContext.getUserId());
            return ApiResponse.success("Left event", null);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinlik katılımcılarını listeler")
    @GetMapping("/{eventId}/participants")
    public ApiResponse<PageResponse<EventParticipantResponse>> getParticipants(@PathVariable Long eventId,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "20") int size,
                                                                               HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            Page<EventParticipantResponse> result = eventService.getParticipants(eventId, PageRequest.of(page, size));
            return ApiResponse.success("Participants fetched", toPageResponse(result));
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Katılımcıyı onaylar")
    @PostMapping("/{eventId}/participants/{participantId}/approve")
    public ApiResponse<EventParticipantResponse> approveParticipant(@PathVariable Long eventId,
                                                                    @PathVariable Long participantId,
                                                                    HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventParticipantResponse response = eventService.approveParticipant(eventId, participantId,
                userContext.getUserId());
            return ApiResponse.success("Participant approved", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Katılımcıyı reddeder")
    @PostMapping("/{eventId}/participants/{participantId}/reject")
    public ApiResponse<EventParticipantResponse> rejectParticipant(@PathVariable Long eventId,
                                                                   @PathVariable Long participantId,
                                                                   @Valid @RequestBody RejectParticipantRequest request,
                                                                   HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventParticipantResponse response = eventService.rejectParticipant(eventId, participantId, request,
                userContext.getUserId());
            return ApiResponse.success("Participant rejected", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinliği favorilere ekler")
    @PostMapping("/{eventId}/favorite")
    public ApiResponse<Void> favoriteEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            eventService.favoriteEvent(eventId, userContext.getUserId());
            return ApiResponse.success("Event favorited", null);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinliği favorilerden çıkarır")
    @DeleteMapping("/{eventId}/favorite")
    public ApiResponse<Void> unfavoriteEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            eventService.unfavoriteEvent(eventId, userContext.getUserId());
            return ApiResponse.success("Event unfavorited", null);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Favori etkinliklerimi listeler")
    @GetMapping("/me/favorites")
    public ApiResponse<PageResponse<EventSummaryResponse>> myFavorites(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "20") int size,
                                                                       HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        Page<EventSummaryResponse> result = eventService.getMyFavoriteEvents(userContext.getUserId(),
            PageRequest.of(page, size));
        return ApiResponse.success("Favorites fetched", toPageResponse(result));
    }

    @Operation(summary = "Katıldığım etkinlikleri listeler")
    @GetMapping("/me/joined")
    public ApiResponse<PageResponse<EventSummaryResponse>> myJoined(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "20") int size,
                                                                    HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        Page<EventSummaryResponse> result = eventService.getMyJoinedEvents(userContext.getUserId(),
            PageRequest.of(page, size));
        return ApiResponse.success("Joined events fetched", toPageResponse(result));
    }

    @Operation(summary = "Oluşturduğum etkinlikleri listeler")
    @GetMapping("/me/created")
    public ApiResponse<PageResponse<EventSummaryResponse>> myCreated(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "20") int size,
                                                                     HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        Page<EventSummaryResponse> result = eventService.getMyCreatedEvents(userContext.getUserId(),
            PageRequest.of(page, size));
        return ApiResponse.success("Created events fetched", toPageResponse(result));
    }

    @Operation(summary = "Etkinliği yayınlar")
    @PostMapping("/{eventId}/publish")
    public ApiResponse<EventResponse> publishEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventResponse response = eventService.publishEvent(eventId, userContext.getUserId());
            return ApiResponse.success("Event published", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinliği öne çıkarır")
    @PostMapping("/{eventId}/feature")
    public ApiResponse<EventResponse> featureEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventResponse response = eventService.featureEvent(eventId, userContext.getUserId());
            return ApiResponse.success("Event featured", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinliği öne çıkarmaktan kaldırır")
    @DeleteMapping("/{eventId}/feature")
    public ApiResponse<EventResponse> unfeatureEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventResponse response = eventService.unfeatureEvent(eventId, userContext.getUserId());
            return ApiResponse.success("Event unfeatured", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    @Operation(summary = "Etkinliği tamamlandı olarak işaretler")
    @PostMapping("/{eventId}/complete")
    public ApiResponse<EventResponse> completeEvent(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        UserContext userContext = userContextResolver.resolve(httpRequest);
        try {
            UserContextHolder.set(userContext);
            EventResponse response = eventService.completeEvent(eventId, userContext.getUserId());
            return ApiResponse.success("Event completed", response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private PageResponse<EventSummaryResponse> toPageResponse(Page<EventSummaryResponse> page) {
        return PageResponse.<EventSummaryResponse>builder()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
