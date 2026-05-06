package event_service.publisher;

import event_service.entity.Event;

public interface EventPublisher {

    void publishEventCreated(Event event);

    void publishEventUpdated(Event event);

    void publishEventCancelled(Event event);

    void publishEventJoined(Event event, Long userId);

    void publishEventReminderNeeded(Event event);
}
