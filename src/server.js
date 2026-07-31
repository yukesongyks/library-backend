const express = require('express');
const cors = require('cors');
const store = require('./store');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 3001;

// ---- Error helper ----
function errorBody(message) {
  return { error: message };
}

function handleError(res, err, defaultStatus = 500) {
  if (err && err.code === 'CONFLICT') {
    return res.status(409).json(errorBody(err.message));
  }
  if (err && err.code === 'BAD_REQUEST') {
    return res.status(400).json(errorBody(err.message));
  }
  console.error('Unhandled error:', err);
  return res.status(defaultStatus).json(errorBody(err.message || 'internal error'));
}

// ---- Validation ----
function validateBookInput(body, { allowMissingId = false } = {}) {
  const { title, author, totalCopies } = body || {};
  if (!title || !String(title).trim()) {
    const e = new Error('title is required'); e.code = 'BAD_REQUEST'; throw e;
  }
  if (!author || !String(author).trim()) {
    const e = new Error('author is required'); e.code = 'BAD_REQUEST'; throw e;
  }
  const tc = Number(totalCopies);
  if (!Number.isInteger(tc) || tc < 1) {
    const e = new Error('totalCopies must be integer >= 1'); e.code = 'BAD_REQUEST'; throw e;
  }
  return { title: String(title).trim(), author: String(author).trim(), totalCopies: tc };
}

// ---- Routes: Book CRUD ----
app.post('/api/books', (req, res) => {
  try {
    const data = validateBookInput(req.body);
    const book = store.createBook(data);
    return res.status(201).json(book);
  } catch (err) {
    return handleError(res, err);
  }
});

app.get('/api/books', (_req, res) => {
  res.json(store.listBooks());
});

app.get('/api/books/:id', (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) {
    return res.status(400).json(errorBody('id must be integer'));
  }
  const book = store.getBook(id);
  if (!book) return res.status(404).json(errorBody('book not found'));
  return res.json(book);
});

app.put('/api/books/:id', (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) {
    return res.status(400).json(errorBody('id must be integer'));
  }
  try {
    const data = validateBookInput(req.body);
    const book = store.updateBook(id, data);
    if (!book) return res.status(404).json(errorBody('book not found'));
    return res.json(book);
  } catch (err) {
    return handleError(res, err);
  }
});

app.delete('/api/books/:id', (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) {
    return res.status(400).json(errorBody('id must be integer'));
  }
  try {
    const deleted = store.deleteBook(id);
    if (!deleted) return res.status(404).json(errorBody('book not found'));
    return res.status(204).send();
  } catch (err) {
    return handleError(res, err);
  }
});

// ---- Routes: Borrow / return ----
app.post('/api/books/:id/borrow', (req, res) => {
  const id = Number(req.params.id);
  const borrower = req.body && req.body.borrower;
  if (!borrower || !String(borrower).trim()) {
    return res.status(400).json(errorBody('borrower is required'));
  }
  const result = store.borrowBook(id, String(borrower).trim());
  if (result.status === 'NOT_FOUND') return res.status(404).json(errorBody('book not found'));
  if (result.status === 'CONFLICT') return res.status(409).json(errorBody(result.message));
  return res.json(result.borrow);
});

app.post('/api/books/:id/return', (req, res) => {
  const id = Number(req.params.id);
  const borrowId = req.body && Number(req.body.borrowId);
  if (!Number.isInteger(borrowId)) {
    return res.status(400).json(errorBody('borrowId is required'));
  }
  const result = store.returnBook(id, borrowId);
  if (result.status === 'NOT_FOUND') return res.status(404).json(errorBody('book not found'));
  if (result.status === 'CONFLICT') return res.status(409).json(errorBody(result.message));
  return res.json(result.borrow);
});

// ---- 404 fallback for unknown API paths ----
app.use('/api', (_req, res) => {
  res.status(404).json(errorBody('not found'));
});

app.listen(PORT, () => {
  console.log(`library-backend listening on http://localhost:${PORT}`);
});

module.exports = app;
