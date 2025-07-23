package LibrarySystem.models;

public enum BorrowResult {
    SUCCESS,
    MAX_BOOKS_REACHED,
    BOOK_NOT_AVAILABLE,
    ALREADY_BORROWED,
    HAS_UNPAID_FINES,
    INSUFFICIENT_BALANCE
}
