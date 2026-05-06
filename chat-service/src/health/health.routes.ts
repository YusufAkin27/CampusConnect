import { Router } from "express";
import { successResponse } from "../utils/api-response";

export const healthRoutes = Router();

healthRoutes.get("/health", (req, res) => {
  res.json(successResponse("OK", { status: "UP" }));
});
