export class BaseAppError extends Error {
  public statusCode: number;
  public code: string;
  public details?: Record<string, unknown> | null;

  constructor(statusCode: number, code: string, message: string, details?: Record<string, unknown> | null) {
    super(message);
    this.statusCode = statusCode;
    this.code = code;
    this.details = details ?? null;
  }
}
