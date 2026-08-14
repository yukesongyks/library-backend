/**
 * 返回 "Hello, World!" 字符串
 */
export function sayHello(): string {
  return "Hello, World!";
}

// 直接运行时打印到控制台
if (require.main === module) {
  console.log(sayHello());
}
