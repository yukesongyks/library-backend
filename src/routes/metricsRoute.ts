import { Router } from "express";
import { success, fail } from "../utils/response.js";
import { metricsStore } from "../data/metricsStore.js";

export const metricsRoute = Router();

const VALID_DIMENSIONS = new Set(["type", "level", "dept"]);

metricsRoute.get("/metrics/calls", (req, res) => {
  const dimension = typeof req.query.dimension === "string" ? req.query.dimension : "";
  const range = typeof req.query.range === "string" ? req.query.range : "7d";

  if (!VALID_DIMENSIONS.has(dimension)) {
    res.status(400).json(fail(400, "invalid dimension"));
    return;
  }

  const result = metricsStore.query({
    dimension: dimension as "type" | "level" | "dept",
    range,
  });

  res.json(
    success({
      trend: result.trend,
      distribution: result.distribution,
    }),
  );
});
