package event_service.repository;

import event_service.entity.Event;
import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import event_service.enums.EventType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public final class EventSpecifications {

    private EventSpecifications() {
    }

    public static Specification<Event> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like),
                cb.like(cb.lower(root.get("shortDescription")), like)
            );
        };
    }

    public static Specification<Event> hasCategory(EventCategory category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Event> hasType(EventType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Event> hasStatus(EventStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Event> campusEquals(String campusName) {
        return (root, query, cb) -> {
            if (campusName == null || campusName.isBlank()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("campusName")), campusName.toLowerCase());
        };
    }

    public static Specification<Event> facultyEquals(String faculty) {
        return (root, query, cb) -> {
            if (faculty == null || faculty.isBlank()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("faculty")), faculty.toLowerCase());
        };
    }

    public static Specification<Event> departmentEquals(String department) {
        return (root, query, cb) -> {
            if (department == null || department.isBlank()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("department")), department.toLowerCase());
        };
    }

    public static Specification<Event> startDateFrom(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) {
                return null;
            }
            LocalDateTime from = startDate.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("startDateTime"), from);
        };
    }

    public static Specification<Event> endDateTo(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) {
                return null;
            }
            LocalDateTime to = endDate.plusDays(1).atStartOfDay();
            return cb.lessThan(root.get("startDateTime"), to);
        };
    }

    public static Specification<Event> onlyOnline(Boolean onlyOnline) {
        return (root, query, cb) -> {
            if (onlyOnline == null || !onlyOnline) {
                return null;
            }
            return cb.isTrue(root.get("isOnline"));
        };
    }

    public static Specification<Event> onlyFeatured(Boolean onlyFeatured) {
        return (root, query, cb) -> {
            if (onlyFeatured == null || !onlyFeatured) {
                return null;
            }
            return cb.isTrue(root.get("isFeatured"));
        };
    }
}
