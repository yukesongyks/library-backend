import type { ApiResponse } from "../types/api.js";

export function success<T>(data: T, message?: string): ApiResponse<T> {
  const res: ApiResponse<T> = { code: 0, data };
  if (message !== undefined) {
    res.message = message;
  }
  return res;
}

export function fail(code: number, message: string): ApiResponse<never> {
  return { code, message };
}
