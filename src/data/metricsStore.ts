import type { CallRecord } from "../types/metrics.js";

export type MetricsQueryOptions = {
  dimension: "type" | "level" | "dept";
  range: string;
};

export type MetricsQueryResult = {
  trend: { date: string; count: number }[];
  distribution: { name: string; count: number }[];
};

const dimensionFieldMap: Record<
  MetricsQueryOptions["dimension"],
  keyof CallRecord
> = {
  type: "caller_type",
  level: "caller_level",
  dept: "caller_dept",
};

class MetricsStore {
  private records: CallRecord[] = [];

  record(c: CallRecord): void {
    this.records.push(c);
  }

  query(opts: MetricsQueryOptions): MetricsQueryResult {
    const now = new Date();
    const rangeDays = parseRangeDays(opts.range);

    const sinceMs = now.getTime() - rangeDays * 24 * 60 * 60 * 1000;
    const filtered = this.records.filter((r) => {
      const t = Date.parse(r.call_time);
      if (Number.isNaN(t)) {
        return false;
      }
      return t >= sinceMs;
    });

    const trendMap = new Map<string, number>();
    const distMap = new Map<string, number>();
    const dimField = dimensionFieldMap[opts.dimension];

    for (const r of filtered) {
      const date = r.call_time.slice(0, 10);
      trendMap.set(date, (trendMap.get(date) ?? 0) + 1);
      const dimValue = String(r[dimField] ?? "unknown");
      distMap.set(dimValue, (distMap.get(dimValue) ?? 0) + 1);
    }

    const trend = [...trendMap.entries()]
      .map(([date, count]) => ({ date, count }))
      .sort((a, b) => (a.date < b.date ? -1 : 1));

    const distribution = [...distMap.entries()]
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count);

    return { trend, distribution };
  }
}

function parseRangeDays(range: string): number {
  const match = /^(\d+)d$/.exec(range);
  if (match) {
    const n = Number.parseInt(match[1], 10);
    if (n > 0) {
      return n;
    }
  }
  // Fallback: default to 7 days for unrecognized ranges.
  return 7;
}

export const metricsStore = new MetricsStore();
