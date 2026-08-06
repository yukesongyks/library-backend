import { Router } from "express";
import { success, fail } from "../utils/response.js";
import { bubbleSort } from "../utils/bubbleSort.js";

export const bubbleSortRoute = Router();

type BubbleSortResult = {
  sorted: number[];
};

bubbleSortRoute.post("/bubble-sort", (req, res) => {
  const contentType = req.headers["content-type"] ?? "";
  if (!contentType.includes("application/json")) {
    res.status(415).json(fail(415, "Content-Type must be application/json"));
    return;
  }

  const arr = req.body?.array;

  if (!Array.isArray(arr)) {
    res.status(422).json(fail(422, "array must be number[]"));
    return;
  }

  if (arr.length > 10000) {
    res.status(422).json(fail(422, "array length exceeds 10000"));
    return;
  }

  for (let i = 0; i < arr.length; i++) {
    const v = arr[i];
    if (typeof v !== "number" || !Number.isFinite(v)) {
      res.status(422).json(fail(422, "array must contain finite numbers only"));
      return;
    }
  }

  try {
    const sorted = bubbleSort(arr as number[]);
    const data: BubbleSortResult = { sorted };
    res.json(success(data));
  } catch (err) {
    const message = err instanceof Error ? err.message : "bubble sort failed";
    res.status(500).json(fail(500, message));
  }
});
