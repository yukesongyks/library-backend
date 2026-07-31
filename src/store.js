// In-memory repository for Book and BorrowRecord (MVP).
// State resets on restart — acceptable per proposal non-goals.

// ---- Domain storage ----
let nextBookId = 1;
let nextBorrowId = 1;

/** @type {Record<number, {id:number,title:string,author:string,totalCopies:number,availableCopies:number}>} */
const books = {};

/** @type {Record<number, {id:number,bookId:number,borrower:string,borrowedAt:string,returnedAt:string|null,status:string}>} */
const borrows = {};

// ---- Helpers ----
function bookStatus(book) {
  if (!book) return null;
  return book.availableCopies > 0 ? 'AVAILABLE' : 'UNAVAILABLE';
}

function toBookView(book) {
  if (!book) return null;
  return {
    id: book.id,
    title: book.title,
    author: book.author,
    totalCopies: book.totalCopies,
    availableCopies: book.availableCopies,
    status: bookStatus(book),
  };
}

function toBorrowView(br) {
  if (!br) return null;
  return {
    id: br.id,
    bookId: br.bookId,
    borrower: br.borrower,
    borrowedAt: br.borrowedAt,
    returnedAt: br.returnedAt,
    status: br.status,
  };
}

// ---- Book operations ----
function createBook({ title, author, totalCopies }) {
  const book = {
    id: nextBookId++,
    title: String(title),
    author: String(author),
    totalCopies: Number(totalCopies),
    availableCopies: Number(totalCopies),
  };
  books[book.id] = book;
  return toBookView(book);
}

function listBooks() {
  return Object.keys(books)
    .map((k) => Number(k))
    .sort((a, b) => a - b)
    .map((id) => toBookView(books[id]));
}

function getBook(id) {
  return toBookView(books[id] || null);
}

function updateBook(id, { title, author, totalCopies }) {
  const book = books[id];
  if (!book) return null;
  const borrowedCount = book.totalCopies - book.availableCopies;
  const newTotal = Number(totalCopies);
  if (newTotal < borrowedCount) {
    const err = new Error('totalCopies must be >= borrowed count');
    err.code = 'CONFLICT';
    throw err;
  }
  book.title = String(title);
  book.author = String(author);
  book.totalCopies = newTotal;
  book.availableCopies = newTotal - borrowedCount;
  return toBookView(book);
}

function deleteBook(id) {
  const book = books[id];
  if (!book) return false;
  const borrowedCount = book.totalCopies - book.availableCopies;
  if (borrowedCount > 0) {
    const err = new Error('cannot delete book with unreturned borrows');
    err.code = 'CONFLICT';
    throw err;
  }
  delete books[id];
  return true;
}

// ---- Borrow / return operations ----
function borrowBook(id, borrower) {
  const book = books[id];
  if (!book) return { status: 'NOT_FOUND' };
  if (book.availableCopies < 1) return { status: 'CONFLICT', message: 'no available copies' };

  book.availableCopies -= 1;
  const record = {
    id: nextBorrowId++,
    bookId: id,
    borrower: String(borrower),
    borrowedAt: new Date().toISOString(),
    returnedAt: null,
    status: 'BORROWED',
  };
  borrows[record.id] = record;
  return { status: 'OK', borrow: toBorrowView(record) };
}

function returnBook(bookId, borrowId) {
  const book = books[bookId];
  if (!book) return { status: 'NOT_FOUND' };
  const record = borrows[borrowId];
  if (!record || record.bookId !== bookId || record.status !== 'BORROWED') {
    return { status: 'CONFLICT', message: 'invalid borrow record' };
  }
  record.status = 'RETURNED';
  record.returnedAt = new Date().toISOString();
  if (book.availableCopies < book.totalCopies) {
    book.availableCopies += 1;
  }
  return { status: 'OK', borrow: toBorrowView(record) };
}

module.exports = {
  createBook,
  listBooks,
  getBook,
  updateBook,
  deleteBook,
  borrowBook,
  returnBook,
  // exposed for tests/reset
  _reset: () => {
    for (const k of Object.keys(books)) delete books[k];
    for (const k of Object.keys(borrows)) delete borrows[k];
    nextBookId = 1;
    nextBorrowId = 1;
  },
};
