import { BaseAppError } from "./base.error";

export class UnauthorizedError extends BaseAppError {
  constructor(message = "Unauthorized") {
    super(401, "UNAUTHORIZED", message);
  }
}

export class ForbiddenError extends BaseAppError {
  constructor(message = "Forbidden") {
    super(403, "FORBIDDEN", message);
  }
}

export class ValidationError extends BaseAppError {
  constructor(message = "Validation failed", details?: Record<string, unknown> | null) {
    super(400, "VALIDATION_ERROR", message, details);
  }
}

export class ConversationNotFoundError extends BaseAppError {
  constructor(message = "Conversation not found") {
    super(404, "CONVERSATION_NOT_FOUND", message);
  }
}

export class ConversationAccessDeniedError extends BaseAppError {
  constructor(message = "Conversation access denied") {
    super(403, "CONVERSATION_ACCESS_DENIED", message);
  }
}

export class DirectConversationAlreadyExistsError extends BaseAppError {
  constructor(message = "Direct conversation already exists") {
    super(409, "DIRECT_CONVERSATION_EXISTS", message);
  }
}

export class UserNotFoundError extends BaseAppError {
  constructor(message = "User not found") {
    super(404, "USER_NOT_FOUND", message);
  }
}

export class UserBlockedError extends BaseAppError {
  constructor(message = "User is blocked") {
    super(403, "USER_BLOCKED", message);
  }
}

export class MessageNotFoundError extends BaseAppError {
  constructor(message = "Message not found") {
    super(404, "MESSAGE_NOT_FOUND", message);
  }
}

export class MessageEditWindowExpiredError extends BaseAppError {
  constructor(message = "Message edit window expired") {
    super(409, "MESSAGE_EDIT_WINDOW_EXPIRED", message);
  }
}

export class MessageDeleteWindowExpiredError extends BaseAppError {
  constructor(message = "Message delete window expired") {
    super(409, "MESSAGE_DELETE_WINDOW_EXPIRED", message);
  }
}

export class MessageAlreadyDeletedError extends BaseAppError {
  constructor(message = "Message already deleted") {
    super(409, "MESSAGE_ALREADY_DELETED", message);
  }
}

export class ParticipantNotFoundError extends BaseAppError {
  constructor(message = "Participant not found") {
    super(404, "PARTICIPANT_NOT_FOUND", message);
  }
}

export class ParticipantAlreadyExistsError extends BaseAppError {
  constructor(message = "Participant already exists") {
    super(409, "PARTICIPANT_ALREADY_EXISTS", message);
  }
}

export class ParticipantPermissionDeniedError extends BaseAppError {
  constructor(message = "Participant permission denied") {
    super(403, "PARTICIPANT_PERMISSION_DENIED", message);
  }
}

export class GroupOwnerRequiredError extends BaseAppError {
  constructor(message = "Group owner required") {
    super(403, "GROUP_OWNER_REQUIRED", message);
  }
}

export class GroupAdminRequiredError extends BaseAppError {
  constructor(message = "Group admin required") {
    super(403, "GROUP_ADMIN_REQUIRED", message);
  }
}

export class MediaValidationFailedError extends BaseAppError {
  constructor(message = "Media validation failed") {
    super(409, "MEDIA_VALIDATION_FAILED", message);
  }
}

export class ExternalServiceUnavailableError extends BaseAppError {
  constructor(message = "External service unavailable") {
    super(503, "EXTERNAL_SERVICE_UNAVAILABLE", message);
  }
}

export class RateLimitExceededError extends BaseAppError {
  constructor(message = "Rate limit exceeded") {
    super(429, "RATE_LIMIT_EXCEEDED", message);
  }
}
