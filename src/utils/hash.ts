import { createHash } from "node:crypto";

export function computeHash(
  input: string,
  algorithm: "sha256" | "md5",
): string {
  return createHash(algorithm).update(input, "utf8").digest("hex");
}
