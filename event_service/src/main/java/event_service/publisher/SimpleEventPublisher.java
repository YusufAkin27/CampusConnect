package event_service.publisher;

import event_service.entity.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleEventPublisher implements EventPublisher {

    @Override
    public void publishEventCreated(Event event) {
        log.info("Event created: {}", event.getId());
    }

    @Override
    public void publishEventUpdated(Event event) {
        log.info("Event updated: {}", event.getId());
    }

    @Override
    public void publishEventCancelled(Event event) {
        log.info("Event cancelled: {}", event.getId());
    }

    @Override
    public void publishEventJoined(Event event, Long userId) {
        log.info("Event joined: {} by user {}", event.getId(), userId);
    }

    @Override
    public void publishEventReminderNeeded(Event event) {
        log.info("Event reminder needed: {}", event.getId());
    }
}
