import express from "express";
import { callerIdMiddleware } from "./middleware/callerIdMiddleware.js";
import { metricsMiddleware } from "./middleware/metricsMiddleware.js";
import { errorMiddleware } from "./middleware/errorMiddleware.js";
import { helloworldRoute } from "./routes/helloworldRoute.js";
import { hashRoute } from "./routes/hashRoute.js";
import { bubbleSortRoute } from "./routes/bubbleSortRoute.js";
import { exportRoute } from "./routes/exportRoute.js";
import { metricsRoute } from "./routes/metricsRoute.js";

export const app = express();

app.use(express.json());
app.use(callerIdMiddleware);
app.use(metricsMiddleware);

app.use("/api", helloworldRoute);
app.use("/api", hashRoute);
app.use("/api", bubbleSortRoute);
app.use("/api", exportRoute);
app.use("/api", metricsRoute);

app.use(errorMiddleware);
