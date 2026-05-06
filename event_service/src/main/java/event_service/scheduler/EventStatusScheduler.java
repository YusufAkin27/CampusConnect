package event_service.scheduler;

import event_service.entity.Event;
import event_service.enums.EventStatus;
import event_service.publisher.EventPublisher;
import event_service.repository.EventRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "event.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class EventStatusScheduler {

    private final EventRepository eventRepository;
    private final EventPublisher eventPublisher;

    @Scheduled(fixedDelayString = "600000")
    @Transactional
    public void completeFinishedEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventRepository
            .findByStatus(EventStatus.PUBLISHED, PageRequest.of(0, 200))
            .getContent();
        for (Event event : events) {
            if (event.getEndDateTime() != null && event.getEndDateTime().isBefore(now)) {
                event.setStatus(EventStatus.COMPLETED);
                eventRepository.save(event);
                log.info("Marked event {} as completed", event.getId());
            }
        }
    }

    @Scheduled(fixedDelayString = "1800000")
    public void publishReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime upcoming = now.plusHours(2);
        List<Event> events = eventRepository
            .findByStartDateTimeAfter(now, PageRequest.of(0, 200))
            .getContent();
        for (Event event : events) {
            if (event.getStartDateTime() != null && !event.getStartDateTime().isAfter(upcoming)) {
                eventPublisher.publishEventReminderNeeded(event);
            }
        }
    }
}
