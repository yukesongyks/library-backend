import { Router } from "express";
import { success, fail } from "../utils/response.js";
import { bubbleSort } from "../utils/bubbleSort.js";

export const bubbleSortRoute = Router();

type BubbleSortResult = {
  sorted: number[];
};

bubbleSortRoute.post("/bubble-sort", (req, res) => {
  const arr = req.body?.array;

  if (!Array.isArray(arr) || !arr.every((v) => typeof v === "number")) {
    res.status(422).json(fail(422, "array must be number[]"));
    return;
  }

  if (arr.length > 10000) {
    res.status(422).json(fail(422, "array length exceeds 10000"));
    return;
  }

  const sorted = bubbleSort(arr as number[]);
  const data: BubbleSortResult = { sorted };
  res.json(success(data));
});
