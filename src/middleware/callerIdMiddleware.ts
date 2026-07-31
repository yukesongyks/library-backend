import type { Request, Response, NextFunction } from "express";

export function callerIdMiddleware(
  req: Request,
  res: Response,
  next: NextFunction,
): void {
  const header = req.header("X-Caller-Id");
  req.callerId = header && header.trim().length > 0 ? header.trim() : "demo-user";
  next();
}
