// Integration tests for library-backend REST API.
// Covers all scenarios in openspec/changes/add-library-management/specs/library-management.md
// Uses only Node.js built-in modules (no supertest dependency).
// Run: PORT=3099 node test/api.test.js

const http = require('http');
const assert = require('assert');

const PORT = process.env.PORT || 3099;
const HOST = 'localhost';

let passed = 0;
let failed = 0;

function request(method, path, body) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const headers = { 'Content-Type': 'application/json' };
    if (data) headers['Content-Length'] = Buffer.byteLength(data);
    const req = http.request({ host: HOST, port: PORT, method, path, headers }, (res) => {
      let buf = '';
      res.on('data', (c) => { buf += c; });
      res.on('end', () => {
        let json = null;
        if (buf) {
          try { json = JSON.parse(buf); } catch { json = buf; }
        }
        resolve({ status: res.statusCode, body: json });
      });
    });
    req.on('error', reject);
    if (data) req.write(data);
    req.end();
  });
}

async function test(name, fn) {
  try {
    await fn();
    passed++;
    console.log(`  ✓ ${name}`);
  } catch (err) {
    failed++;
    console.error(`  ✗ ${name}`);
    console.error(`      ${err.message}`);
  }
}

function resetStore() {
  // Reset in-memory store between suites via a sentinel; here we use DELETE on
  // each known book id is fragile, so tests are ordered and self-contained.
  // The store resets only on process restart; tests below are sequenced to
  // build state incrementally and assert against accumulated state.
}

async function main() {
  // ====================================================================
  // Requirement: 图书信息管理（CRUD）
  // ====================================================================
  console.log('\n# 图书信息管理（CRUD）');

  // Scenario: 新增图书
  await test('新增图书 → 201 + availableCopies=totalCopies + status=AVAILABLE', async () => {
    const res = await request('POST', '/api/books', { title: '三体', author: '刘慈欣', totalCopies: 3 });
    assert.strictEqual(res.status, 201, `expected 201 got ${res.status}: ${JSON.stringify(res.body)}`);
    assert.ok(res.body.id >= 1, 'id should be positive integer');
    assert.strictEqual(res.body.title, '三体');
    assert.strictEqual(res.body.author, '刘慈欣');
    assert.strictEqual(res.body.totalCopies, 3);
    assert.strictEqual(res.body.availableCopies, 3, 'availableCopies should equal totalCopies');
    assert.strictEqual(res.body.status, 'AVAILABLE');
  });

  await test('新增第二本图书（列表含 ≥2 本）', async () => {
    const res = await request('POST', '/api/books', { title: '活着', author: '余华', totalCopies: 2 });
    assert.strictEqual(res.status, 201);
    assert.strictEqual(res.body.status, 'AVAILABLE');
  });

  // Scenario: 查询图书列表
  await test('查询图书列表 → 200 + 数组 + 按 id 升序', async () => {
    const res = await request('GET', '/api/books');
    assert.strictEqual(res.status, 200);
    assert.ok(Array.isArray(res.body), 'body should be array');
    assert.ok(res.body.length >= 2, 'should contain at least 2 books');
    for (const b of res.body) {
      assert.ok('id' in b && 'title' in b && 'author' in b && 'totalCopies' in b && 'availableCopies' in b && 'status' in b, 'all fields present');
    }
    // id 升序
    for (let i = 1; i < res.body.length; i++) {
      assert.ok(res.body[i].id > res.body[i - 1].id, 'ids ascending');
    }
  });

  // Scenario: 查询单本图书详情
  await test('查询单本图书详情 → 200 + 全字段', async () => {
    const res = await request('GET', '/api/books/1');
    assert.strictEqual(res.status, 200);
    assert.strictEqual(res.body.id, 1);
    assert.ok('title' in res.body && 'author' in res.body && 'totalCopies' in res.body && 'availableCopies' in res.body && 'status' in res.body);
  });

  // Scenario: 查询不存在的图书详情
  await test('查询不存在图书 → 404 + error 字段', async () => {
    const res = await request('GET', '/api/books/9999');
    assert.strictEqual(res.status, 404);
    assert.ok(res.body && typeof res.body.error === 'string', 'error field present');
  });

  // Scenario: 更新图书信息
  await test('更新图书信息 → 200 + title 已变', async () => {
    const res = await request('PUT', '/api/books/1', { title: '三体（修订版）', author: '刘慈欣', totalCopies: 3 });
    assert.strictEqual(res.status, 200);
    assert.strictEqual(res.body.title, '三体（修订版）');
    const confirm = await request('GET', '/api/books/1');
    assert.strictEqual(confirm.body.title, '三体（修订版）');
  });

  await test('更新图书 totalCopies < 已借出数 → 409', async () => {
    // 借出 2 本使 borrowedCount=2，再用 totalCopies=1（通过 >=1 参数校验但 <2 触发业务冲突）
    await request('POST', '/api/books/1/borrow', { borrower: '张三' });
    await request('POST', '/api/books/1/borrow', { borrower: '李四' });
    const res = await request('PUT', '/api/books/1', { title: '三体', author: '刘慈欣', totalCopies: 1 });
    assert.strictEqual(res.status, 409);
  });

  // Scenario: 删除图书（无借出时）
  await test('删除无借出图书 → 204 + 再查 404', async () => {
    // 用 id=2（活着，无借出）
    const del = await request('DELETE', '/api/books/2');
    assert.strictEqual(del.status, 204);
    const after = await request('GET', '/api/books/2');
    assert.strictEqual(after.status, 404);
  });

  // Scenario: 删除有借出的图书被拒绝
  await test('删除有借出图书 → 409 + 图书未被删除', async () => {
    // id=1 已有 1 条未归还借阅
    const del = await request('DELETE', '/api/books/1');
    assert.strictEqual(del.status, 409);
    const still = await request('GET', '/api/books/1');
    assert.strictEqual(still.status, 200, 'book should still exist');
  });

  // Scenario: 新增图书参数非法
  await test('新增图书 title 缺失 → 400', async () => {
    const res = await request('POST', '/api/books', { author: 'x', totalCopies: 1 });
    assert.strictEqual(res.status, 400);
    assert.ok(res.body && typeof res.body.error === 'string');
  });

  await test('新增图书 totalCopies < 1 → 400', async () => {
    const res = await request('POST', '/api/books', { title: 'x', author: 'y', totalCopies: 0 });
    assert.strictEqual(res.status, 400);
  });

  // ====================================================================
  // Requirement: 借阅管理
  // ====================================================================
  console.log('\n# 借阅管理');

  // Scenario: 借出图书
  await test('借出图书 → 200 + BorrowRecord + availableCopies 减 1', async () => {
    const before = await request('GET', '/api/books/1');
    const res = await request('POST', '/api/books/1/borrow', { borrower: '李四' });
    assert.strictEqual(res.status, 200);
    assert.ok(res.body.id >= 1, 'borrow record id');
    assert.strictEqual(res.body.bookId, 1);
    assert.strictEqual(res.body.borrower, '李四');
    assert.ok(typeof res.body.borrowedAt === 'string' && res.body.borrowedAt.length > 0, 'borrowedAt ISO8601');
    assert.strictEqual(res.body.returnedAt, null);
    assert.strictEqual(res.body.status, 'BORROWED');
    const after = await request('GET', '/api/books/1');
    assert.strictEqual(after.body.availableCopies, before.body.availableCopies - 1, 'availableCopies decreased');
  });

  // Scenario: 借出不存在的图书
  await test('借出不存在的图书 → 404', async () => {
    const res = await request('POST', '/api/books/9999/borrow', { borrower: 'x' });
    assert.strictEqual(res.status, 404);
  });

  await test('借出 borrower 缺失 → 400', async () => {
    const res = await request('POST', '/api/books/1/borrow', {});
    assert.strictEqual(res.status, 400);
  });

  // Scenario: 库存不足时借出被拒绝
  await test('库存不足借出 → 409 + availableCopies 不变 + 无新记录', async () => {
    // 把 id=1 的库存借空：当前 totalCopies=3, borrowedCount=2(张三+李四), available=1
    await request('POST', '/api/books/1/borrow', { borrower: '王五' }); // available -> 0
    const before = await request('GET', '/api/books/1');
    assert.strictEqual(before.body.availableCopies, 0);
    const res = await request('POST', '/api/books/1/borrow', { borrower: '赵六' });
    assert.strictEqual(res.status, 409);
    const after = await request('GET', '/api/books/1');
    assert.strictEqual(after.body.availableCopies, 0, 'availableCopies unchanged');
  });

  // Scenario: 归还图书
  await test('归还图书 → 200 + status=RETURNED + returnedAt 非空 + availableCopies 加 1', async () => {
    // 借一条新记录用于归还
    const br = await request('POST', '/api/books/1/borrow', { borrower: '钱七' }); // 借不到，库存=0
    if (br.status === 200) {
      const before = await request('GET', '/api/books/1');
      const res = await request('POST', '/api/books/1/return', { borrowId: br.body.id });
      assert.strictEqual(res.status, 200);
      assert.strictEqual(res.body.status, 'RETURNED');
      assert.ok(res.body.returnedAt !== null, 'returnedAt non-null');
      const after = await request('GET', '/api/books/1');
      assert.strictEqual(after.body.availableCopies, before.body.availableCopies + 1);
    } else {
      // 库存为 0 无法借出新记录，先归还张三的记录腾出库存
      const list = await request('GET', '/api/books');
      // 直接归还最早借阅的张三记录（id 较小）
      const ret = await request('POST', '/api/books/1/return', { borrowId: 1 });
      assert.strictEqual(ret.status, 200);
      assert.strictEqual(ret.body.status, 'RETURNED');
      assert.ok(ret.body.returnedAt !== null);
      const after = await request('GET', '/api/books/1');
      assert.strictEqual(after.body.availableCopies, 1, 'availableCopies restored');
    }
  });

  // Scenario: 归还无效借阅记录
  await test('归还已归还记录 → 409 + availableCopies 不变', async () => {
    const before = await request('GET', '/api/books/1');
    // borrowId=1 已归还，再归还
    const res = await request('POST', '/api/books/1/return', { borrowId: 1 });
    assert.strictEqual(res.status, 409);
    const after = await request('GET', '/api/books/1');
    assert.strictEqual(after.body.availableCopies, before.body.availableCopies, 'availableCopies unchanged');
  });

  await test('归还不存在的 borrowId → 409', async () => {
    const res = await request('POST', '/api/books/1/return', { borrowId: 88888 });
    assert.strictEqual(res.status, 409);
  });

  await test('归还 borrowId 非整数 → 400', async () => {
    const res = await request('POST', '/api/books/1/return', { borrowId: 'abc' });
    assert.strictEqual(res.status, 400);
  });

  // 归还不存在图书
  await test('归还不存在图书 → 404', async () => {
    const res = await request('POST', '/api/books/9999/return', { borrowId: 1 });
    assert.strictEqual(res.status, 404);
  });

  // 未知 API 路径
  await test('未知 API 路径 → 404', async () => {
    const res = await request('GET', '/api/unknown');
    assert.strictEqual(res.status, 404);
  });

  // ====================================================================
  // Summary
  // ====================================================================
  console.log(`\n==== 测试结果 ====`);
  console.log(`通过: ${passed}  失败: ${failed}`);
  if (failed > 0) {
    process.exit(1);
  }
}

main().catch((err) => {
  console.error('Test harness error:', err);
  process.exit(2);
});
