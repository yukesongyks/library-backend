// FNV-1a 32-bit 常量
const FNV_OFFSET_BASIS = 0x811c9dc5;
const FNV_PRIME = 0x01000193;

/**
 * 计算输入字符串的 FNV-1a 32 位哈希值
 * FNV-1a (Fowler–Noll–Vo) 是一种经典的非加密哈希函数，
 * 速度快、分布均匀，适合哈希表和校验和场景。
 *
 * @param input - 待哈希的字符串
 * @returns 小写十六进制编码的 32 位哈希值（8 个字符）
 */
export function fnv1aHash(input: string): string {
  let hash = FNV_OFFSET_BASIS;

  for (let i = 0; i < input.length; i++) {
    // XOR with the byte value of the character
    hash ^= input.charCodeAt(i);
    // Multiply by FNV prime, keep within 32-bit unsigned range
    hash = Math.imul(hash, FNV_PRIME) >>> 0;
  }

  // 转为 8 位十六进制字符串（补零）
  return hash.toString(16).padStart(8, "0");
}

// 直接运行时打印示例
if (require.main === module) {
  const testInput = "Hello, World!";
  console.log(`Input:  ${testInput}`);
  console.log(`FNV-1a: ${fnv1aHash(testInput)}`);
}
