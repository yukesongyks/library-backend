import { Router } from "express";
import { fail } from "../utils/response.js";
import { buildExportBuffer } from "../utils/exporter.js";

export const exportRoute = Router();

const VALID_TABS = new Set(["helloworld", "hash", "bubble-sort"]);

/**
 * 根据 tab 类型将前端传入的原始数据转换为导出所需的行记录。
 * 数据由前端通过 POST body 传入，确保导出内容与页面展示一致。
 */
function buildDataRows(
  tab: string,
  data: unknown,
): Record<string, unknown>[] {
  switch (tab) {
    case "helloworld":
      return [{ message: typeof data === "string" ? data : "Hello, World!" }];
    case "hash": {
      if (
        typeof data !== "object" ||
        data === null ||
        !("input" in data) ||
        !("algorithm" in data)
      ) {
        return [];
      }
      const d = data as { input: unknown; algorithm: unknown };
      return [
        {
          input: String(d.input ?? ""),
          algorithm: String(d.algorithm ?? "sha256"),
          hash: String((d as Record<string, unknown>).hash ?? ""),
        },
      ];
    }
    case "bubble-sort": {
      if (!Array.isArray(data)) {
        return [];
      }
      return [{ sorted: data }];
    }
    default:
      return [];
  }
}

exportRoute.post("/export", async (req, res) => {
  const contentType = req.headers["content-type"] ?? "";
  if (!contentType.includes("application/json")) {
    res.status(415).json(fail(415, "Content-Type must be application/json"));
    return;
  }

  const { tab, data, format } = req.body as {
    tab?: unknown;
    data?: unknown;
    format?: unknown;
  };

  const tabStr = typeof tab === "string" ? tab : "";
  const formatStr = typeof format === "string" ? format : "xlsx";

  if (!VALID_TABS.has(tabStr)) {
    res.status(400).json(fail(400, "invalid tab"));
    return;
  }

  if (formatStr !== "xlsx") {
    res.status(400).json(fail(400, "invalid format"));
    return;
  }

  try {
    const dataRows = buildDataRows(tabStr, data);

    if (dataRows.length === 0) {
      res.status(422).json(fail(422, "no valid data to export"));
      return;
    }

    if (dataRows.length > 10000) {
      res.status(422).json(fail(422, "数据量过大，请筛选后导出"));
      return;
    }

    const buffer = await buildExportBuffer(tabStr, dataRows);

    const encodedFilename = encodeURIComponent(`${tabStr}.xlsx`);
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
