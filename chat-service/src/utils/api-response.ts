export type ApiSuccess<T> = {
  success: true;
  message: string;
  data: T;
  timestamp: string;
};

export type ApiFailure = {
  success: false;
  message: string;
  code: string;
  details?: Record<string, unknown> | null;
  timestamp: string;
};

export function successResponse<T>(message: string, data: T): ApiSuccess<T> {
  return {
    success: true,
    message,
    data,
    timestamp: new Date().toISOString()
  };
}

export function failureResponse(message: string, code: string, details?: Record<string, unknown> | null): ApiFailure {
  return {
    success: false,
    message,
    code,
    details: details ?? null,
    timestamp: new Date().toISOString()
  };
}
