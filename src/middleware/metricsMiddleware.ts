import type { Request, Response, NextFunction } from "express";
import { metricsStore } from "../data/metricsStore.js";
import { getCallerProfile } from "../data/callerProfile.js";
import type { CallRecord } from "../types/metrics.js";

const TRACKED_PATHS = new Set<string>([
  "/api/helloworld",
  "/api/hash",
  "/api/bubble-sort",
]);

export function metricsMiddleware(
  req: Request,
  res: Response,
  next: NextFunction,
): void {
  try {
    if (!TRACKED_PATHS.has(req.path)) {
      next();
      return;
    }

    res.on("finish", () => {
      try {
        const callerId = req.callerId ?? "demo-user";
        const profile = getCallerProfile(callerId);
        const record: CallRecord = {
          api_name: req.path,
          caller_id: profile.caller_id,
          caller_type: profile.caller_type,
          caller_level: profile.caller_level,
          caller_dept: profile.caller_dept,
          call_time: new Date().toISOString(),
        };
        metricsStore.record(record);
      } catch (err) {
        console.error("[metricsMiddleware] record failed:", err);
      }
    });
  } catch (err) {
    console.error("[metricsMiddleware] setup failed:", err);
  }

  next();
}
