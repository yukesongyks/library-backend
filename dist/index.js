"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const crypto_1 = __importDefault(require("crypto"));
const app = (0, express_1.default)();
const PORT = process.env.PORT || 3000;
// Middleware
app.use((0, cors_1.default)());
app.use(express_1.default.json({ limit: '1mb' }));
// JSON 解析失败兜底
app.use((err, _req, res, next) => {
    if (err.type === 'entity.parse.failed' || (err instanceof SyntaxError && 'body' in err)) {
        res.status(400).json({ error: 'Invalid JSON in request body' });
        return;
    }
    next(err);
});
// ==================== 1. HelloWorld ====================
app.get('/api/algorithms/helloworld', (_req, res) => {
    res.json({ result: 'Hello, World!' });
});
app.post('/api/algorithms/hash', (req, res) => {
    const { text, algorithm: rawAlgorithm = 'sha256' } = req.body;
    // 归一化算法名：前端可能发送 'SHA-256'、'MD5' 等，统一转小写去连字符
    const algorithm = rawAlgorithm.toLowerCase().replace(/-/g, '');
    if (typeof text !== 'string') {
        res.status(400).json({ error: 'text must be a string' });
        return;
    }
    const supportedAlgorithms = ['md5', 'sha1', 'sha256', 'sha512'];
    if (!supportedAlgorithms.includes(algorithm)) {
        res.status(400).json({
            error: `Unsupported algorithm. Supported: ${supportedAlgorithms.join(', ')}`
        });
        return;
    }
    try {
        const hash = crypto_1.default.createHash(algorithm).update(text).digest('hex');
        res.json({ result: hash, algorithm });
    }
    catch (err) {
        res.status(500).json({ error: 'Hash computation failed' });
    }
});
function bubbleSort(arr) {
    const result = [...arr];
    const n = result.length;
    for (let i = 0; i < n - 1; i++) {
        let swapped = false;
        for (let j = 0; j < n - 1 - i; j++) {
            if (result[j] > result[j + 1]) {
                [result[j], result[j + 1]] = [result[j + 1], result[j]];
                swapped = true;
            }
        }
        if (!swapped)
            break;
    }
    return result;
}
app.post('/api/algorithms/bubble-sort', (req, res) => {
    const { numbers } = req.body;
    if (!Array.isArray(numbers)) {
        res.status(400).json({ error: 'numbers must be an array of numbers' });
        return;
    }
    if (!numbers.every((v) => typeof v === 'number' && Number.isFinite(v))) {
        res.status(400).json({ error: 'All elements must be finite numbers' });
        return;
    }
    const sorted = bubbleSort(numbers);
    const response = {
        result: sorted,
        input: [...numbers]
    };
    res.json(response);
});
// ==================== 4. Export CSV ====================
// GET /api/algorithms/export?type=helloworld|hash|bubble-sort
// 返回该类型最近调用记录的 CSV 文件流（演示模式返回示例数据）
function escapeCsvField(value) {
    const str = String(value ?? '');
    if (str.includes(',') || str.includes('"') || str.includes('\n')) {
        return `"${str.replace(/"/g, '""')}"`;
    }
    return str;
}
function buildDemoCsv(type) {
    switch (type) {
        case 'helloworld': {
            const rows = [
                ['timestamp', 'result'],
                [new Date().toISOString(), 'Hello, World!']
            ];
            return rows.map((r) => r.map(escapeCsvField).join(',')).join('\n');
        }
        case 'hash': {
            const demoHash = crypto_1.default.createHash('sha256').update('demo').digest('hex');
            const rows = [
                ['timestamp', 'text', 'algorithm', 'result'],
                [new Date().toISOString(), 'demo', 'sha256', demoHash]
            ];
            return rows.map((r) => r.map(escapeCsvField).join(',')).join('\n');
        }
        case 'bubble-sort': {
            const rows = [
                ['timestamp', 'input', 'result'],
                [new Date().toISOString(), '[5,3,8,1]', '[1,3,5,8]']
            ];
            return rows.map((r) => r.map(escapeCsvField).join(',')).join('\n');
        }
        default:
            return '';
    }
}
app.get('/api/algorithms/export', (req, res) => {
    const type = req.query.type;
    const validTypes = ['helloworld', 'hash', 'bubble-sort'];
    if (!type || !validTypes.includes(type)) {
        res.status(400).json({ error: `Invalid type. Valid: ${validTypes.join(', ')}` });
        return;
    }
    const csv = buildDemoCsv(type);
    if (!csv) {
        res.status(500).json({ error: 'Failed to generate CSV content' });
        return;
    }
    const filename = `${type}-export.csv`;
    res.setHeader('Content-Type', 'text/csv; charset=utf-8');
    res.setHeader('Content-Disposition', `attachment; filename="${filename}"`);
    res.send(csv);
});
// ==================== 404 Fallback ====================
app.use((_req, res) => {
    res.status(404).json({ error: 'Route not found' });
});
// ==================== Global Error Handling ====================
app.use((err, _req, res, _next) => {
    console.error('Unhandled error:', err);
    const isDev = process.env.NODE_ENV !== 'production';
    res.status(500).json({
        error: 'Internal server error',
        ...(isDev && { detail: err.message })
    });
});
// ==================== Start Server ====================
app.listen(PORT, () => {
    console.log(`library-backend listening on port ${PORT}`);
});
exports.default = app;
//# sourceMappingURL=index.js.map