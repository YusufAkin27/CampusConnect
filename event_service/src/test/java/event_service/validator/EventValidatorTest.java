package event_service.validator;

import event_service.entity.Event;
import event_service.exception.EventCapacityFullException;
import event_service.exception.EventDateInvalidException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EventValidatorTest {

    private final EventValidator validator = new EventValidator();

    @Test
    void shouldFailWhenEndBeforeStart() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = start.minusHours(1);
        Assertions.assertThrows(EventDateInvalidException.class,
            () -> validator.validateEventDates(start, end));
    }

    @Test
    void shouldFailWhenCapacityFull() {
        Event event = Event.builder()
            .capacity(10)
            .participantCount(10)
            .build();
        Assertions.assertThrows(EventCapacityFullException.class,
            () -> validator.validateCapacity(event));
    }
}
