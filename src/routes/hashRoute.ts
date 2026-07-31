import { Router } from "express";
import { success, fail } from "../utils/response.js";
import { computeHash } from "../utils/hash.js";

export const hashRoute = Router();

type HashRequestBody = {
  input?: unknown;
  algorithm?: unknown;
};

type HashResult = {
  input: string;
  algorithm: "sha256" | "md5";
  hash: string;
};

hashRoute.post("/hash", (req, res) => {
  const body = (req.body ?? {}) as HashRequestBody;

  if (typeof body.input !== "string" || body.input.length === 0) {
    res.status(400).json(fail(400, "input is required"));
    return;
  }

  let algorithm: "sha256" | "md5";
  let message: string | undefined;

  if (body.algorithm === "sha256" || body.algorithm === "md5") {
    algorithm = body.algorithm;
  } else {
    algorithm = "sha256";
    message = "algorithm fallback to sha256";
  }

  const hash = computeHash(body.input, algorithm);
  const data: HashResult = { input: body.input, algorithm, hash };
  res.json(success(data, message));
});
