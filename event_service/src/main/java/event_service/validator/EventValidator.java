package event_service.validator;

import event_service.entity.Event;
import event_service.exception.EventAlreadyCancelledException;
import event_service.exception.EventAlreadyCompletedException;
import event_service.exception.EventAlreadyStartedException;
import event_service.exception.EventCapacityFullException;
import event_service.exception.EventDateInvalidException;
import event_service.exception.EventRegistrationClosedException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {

    public void validateEventDates(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new EventDateInvalidException("Event dates are required");
        }
        if (!startDateTime.isBefore(endDateTime)) {
            throw new EventDateInvalidException("Event start date must be before end date");
        }
    }

    public void validateRegistrationDates(LocalDateTime registrationStart, LocalDateTime registrationEnd, LocalDateTime startDateTime) {
        if (registrationEnd != null && startDateTime != null && registrationEnd.isAfter(startDateTime)) {
            throw new EventDateInvalidException("Registration end must be before or equal to event start");
        }
        if (registrationStart != null && registrationEnd != null && registrationStart.isAfter(registrationEnd)) {
            throw new EventDateInvalidException("Registration start must be before registration end");
        }
    }

    public void validateCapacity(Event event) {
        if (event.getCapacity() != null && event.getParticipantCount() >= event.getCapacity()) {
            throw new EventCapacityFullException("Event capacity is full");
        }
    }

    public void validateJoinable(Event event) {
        if (event.isCancelled()) {
            throw new EventAlreadyCancelledException("Event is cancelled");
        }
        if (event.getStatus() == event_service.enums.EventStatus.COMPLETED) {
            throw new EventAlreadyCompletedException("Event already completed");
        }
        LocalDateTime now = LocalDateTime.now();
        if (event.getStartDateTime() != null && !now.isBefore(event.getStartDateTime())) {
            throw new EventAlreadyStartedException("Event already started");
        }
        if (event.getRegistrationEndDateTime() != null && now.isAfter(event.getRegistrationEndDateTime())) {
            throw new EventRegistrationClosedException("Registration is closed");
        }
        validateCapacity(event);
    }

    public void validateUpdatable(Event event) {
        if (event.isCancelled()) {
            throw new EventAlreadyCancelledException("Event already cancelled");
        }
        if (event.getStatus() == event_service.enums.EventStatus.COMPLETED) {
            throw new EventAlreadyCompletedException("Event already completed");
        }
    }

    public void validateCancelable(Event event) {
        if (event.isCancelled()) {
            throw new EventAlreadyCancelledException("Event already cancelled");
        }
        if (event.getStatus() == event_service.enums.EventStatus.COMPLETED) {
            throw new EventAlreadyCompletedException("Event already completed");
        }
    }

    public void validatePublishable(Event event) {
        if (event.isCancelled()) {
            throw new EventAlreadyCancelledException("Event already cancelled");
        }
        if (event.getStatus() == event_service.enums.EventStatus.COMPLETED) {
            throw new EventAlreadyCompletedException("Event already completed");
        }
    }
}
