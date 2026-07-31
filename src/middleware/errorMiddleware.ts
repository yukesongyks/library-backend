import type { Request, Response, NextFunction } from "express";
import type { ApiResponse } from "../types/api.js";

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function errorMiddleware(
  err: unknown,
  _req: Request,
  res: Response,
  _next: NextFunction,
): void {
  console.error("[errorMiddleware] unhandled error:", err);
  const body: ApiResponse<never> = { code: 500, message: "internal error" };
  res.status(500).json(body);
}
