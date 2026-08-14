import { createHash } from "crypto";

/**
 * 计算输入字符串的 SHA-256 哈希值
 * @param input - 待哈希的字符串
 * @returns 小写十六进制编码的 SHA-256 摘要
 */
export function sha256Hash(input: string): string {
  return createHash("sha256").update(input, "utf-8").digest("hex");
}

// 直接运行时打印示例
if (require.main === module) {
  const testInput = "Hello, World!";
  console.log(`Input:   ${testInput}`);
  console.log(`SHA-256: ${sha256Hash(testInput)}`);
}
