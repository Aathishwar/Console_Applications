package LibrarySystem.models;

import java.time.LocalDate;

public class FineRecord {
    private String borrowerEmail;
    private String isbn;
    private double amount;
    private FineReason reason;
    private LocalDate fineDate;
    private boolean paid = false;
    
    public FineRecord(String borrowerEmail, String isbn, double amount, FineReason reason, LocalDate fineDate) {
        this.borrowerEmail = borrowerEmail;
        this.isbn = isbn;
        this.amount = amount;
        this.reason = reason;
        this.fineDate = fineDate;
    }
    
    // Getters and Setters
    public String getBorrowerEmail() { return borrowerEmail; }
    public String getIsbn() { return isbn; }
    public double getAmount() { return amount; }
    public FineReason getReason() { return reason; }
    public LocalDate getFineDate() { return fineDate; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}
