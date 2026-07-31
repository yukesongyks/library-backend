import { Router } from "express";
import { success } from "../utils/response.js";

export const helloworldRoute = Router();

helloworldRoute.get("/helloworld", (_req, res) => {
  res.json(success({ message: "Hello, World!" }));
});
