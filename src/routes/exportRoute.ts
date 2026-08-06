import { Router } from "express";
import { fail } from "../utils/response.js";
import { buildExportBuffer } from "../utils/exporter.js";
import { computeHash } from "../utils/hash.js";
import { bubbleSort } from "../utils/bubbleSort.js";

export const exportRoute = Router();

const VALID_TABS = new Set(["helloworld", "hash", "bubble-sort"]);

function buildDataRows(tab: string): Record<string, unknown>[] {
  switch (tab) {
    case "helloworld":
      return [{ message: "Hello, World!" }];
    case "hash": {
      const input = "";
      const algorithm = "sha256" as const;
      const hash = computeHash(input, algorithm);
      return [{ input, algorithm, hash }];
    }
    case "bubble-sort":
      return [{ sorted: bubbleSort([]) }];
    default:
      return [];
  }
}

exportRoute.get("/export", async (req, res) => {
  const tab = typeof req.query.tab === "string" ? req.query.tab : "";
  const format =
    typeof req.query.format === "string" ? req.query.format : "xlsx";

  if (!VALID_TABS.has(tab)) {
    res.status(400).json(fail(400, "invalid tab"));
    return;
  }

  if (format !== "xlsx") {
    res.status(400).json(fail(400, "invalid format"));
    return;
  }

  try {
    const dataRows = buildDataRows(tab);

    if (dataRows.length > 10000) {
      res.status(422).json(fail(422, "数据量过大，请筛选后导出"));
      return;
    }

    const buffer = await buildExportBuffer(tab, dataRows);

    const encodedFilename = encodeURIComponent(`${tab}.xlsx`);
    res.setHeader(
      "Content-Type",
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    );
    res.setHeader(
      "Content-Disposition",
      `attachment; filename="${encodedFilename}"; filename*=UTF-8''${encodedFilename}`,
    );
    res.send(buffer);
  } catch (err) {
    const message = err instanceof Error ? err.message : "export failed";
    res.status(500).json(fail(500, message));
  }
});
