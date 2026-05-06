import { NextFunction, Request, Response } from "express";
import { Prisma } from "@prisma/client";
import { ZodError } from "zod";
import { BaseAppError } from "../errors/base.error";
import { failureResponse } from "../utils/api-response";
import { logger } from "../utils/logger";

export function errorMiddleware(err: Error, req: Request, res: Response, next: NextFunction): void {
  if (err instanceof BaseAppError) {
    res.status(err.statusCode).json(failureResponse(err.message, err.code, err.details));
    return;
  }

  if (err instanceof ZodError) {
    res.status(400).json(failureResponse("Validation failed", "VALIDATION_ERROR", {
      issues: err.issues
    }));
    return;
  }

  if (err instanceof Prisma.PrismaClientKnownRequestError) {
    res.status(400).json(failureResponse("Database error", "DATABASE_ERROR", {
      code: err.code
    }));
    return;
  }

  logger.error("Unhandled error", { err });
  res.status(500).json(failureResponse("Internal server error", "INTERNAL_ERROR"));
}
