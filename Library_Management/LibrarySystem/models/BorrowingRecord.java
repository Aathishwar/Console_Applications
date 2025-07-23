package LibrarySystem.models;

import java.time.LocalDate;

public class BorrowingRecord {
    private String borrowerEmail;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int extensions = 0;
    
    public BorrowingRecord(String borrowerEmail, String isbn, LocalDate borrowDate, LocalDate dueDate) {
        this.borrowerEmail = borrowerEmail;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
    public String getBorrowerEmail() { return borrowerEmail; }
    public String getIsbn() { return isbn; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public int getExtensions() { return extensions; }
    public void setExtensions(int extensions) { this.extensions = extensions; }
}
