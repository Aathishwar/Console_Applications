package LibrarySystem.reports;

import LibrarySystem.models.*;
import LibrarySystem.books.BookService;
import LibrarySystem.transactions.TransactionService;
import LibrarySystem.auth.AuthService;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private BookService bookService;
    private TransactionService transactionService;
    private AuthService authService;
    
    public ReportService(BookService bookService, TransactionService transactionService, AuthService authService) {
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.authService = authService;
    }
    
    // Admin Reports
    public void generateLowQuantityBooksReport(int threshold) {
        System.out.println("\n--- Books with Low Quantity (≤ " + threshold + ") ---");
        List<Book> lowQuantityBooks = bookService.getBooksWithLowQuantity(threshold);
        
        if (lowQuantityBooks.isEmpty()) {
            System.out.println("No books found with quantity ≤ " + threshold);
        } else {
            for (Book book : lowQuantityBooks) {
                System.out.println(book);
            }
        }
    }
    
    public void generateNeverBorrowedBooksReport() {
        System.out.println("\n--- Books Never Borrowed ---");
        Set<String> neverBorrowedIsbns = transactionService.getNeverBorrowedBooks();
        
        if (neverBorrowedIsbns.isEmpty()) {
            System.out.println("All books have been borrowed at least once.");
        } else {
            List<Book> neverBorrowedBooks = new ArrayList<>();
            for (String isbn : neverBorrowedIsbns) {
                Book book = bookService.getBookByIsbn(isbn);
                if (book != null) {
                    neverBorrowedBooks.add(book);
                }
            }
            
            neverBorrowedBooks.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
            for (Book book : neverBorrowedBooks) {
                System.out.println(book);
            }
        }
    }
    
    public void generateMostBorrowedBooksReport() {
        System.out.println("\n--- Most Borrowed Books ---");
        Map<String, Long> borrowCounts = transactionService.getMostBorrowedBooks();
        
        if (borrowCounts.isEmpty()) {
            System.out.println("No books have been borrowed yet.");
            return;
        }
        
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(borrowCounts.entrySet());
        sortedEntries.sort((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()));
        
        int count = 0;
        for (Map.Entry<String, Long> entry : sortedEntries) {
            if (count >= 10) break; // Show top 10
            
            Book book = bookService.getBookByIsbn(entry.getKey());
            if (book != null) {
                System.out.println(book.getTitle() + " - Borrowed " + entry.getValue() + " times");
            }
            count++;
        }
    }
    
    public void generateOutstandingBooksReport(LocalDate checkDate) {
        System.out.println("\n--- Outstanding Books as of " + checkDate.format(DATE_FORMAT) + " ---");
        List<BorrowingRecord> outstandingBooks = transactionService.getOutstandingBooks(checkDate);
        
        if (outstandingBooks.isEmpty()) {
            System.out.println("No outstanding books found.");
        } else {
            for (BorrowingRecord record : outstandingBooks) {
                Book book = bookService.getBookByIsbn(record.getIsbn());
                User borrower = authService.getUserByEmail(record.getBorrowerEmail());
                
                if (book != null && borrower != null) {
                    long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(record.getDueDate(), checkDate);
                    System.out.println(borrower.getName() + " (" + borrower.getEmail() + ") - " +
                        book.getTitle() + " - Overdue by " + daysOverdue + " days");
                }
            }
        }
    }
    
    public void generateBookStatusReport(String isbn) {
        System.out.println("\n--- Book Status Report ---");
        Book book = bookService.getBookByIsbn(isbn.toUpperCase());
        
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        System.out.println("Book: " + book);
        
        BorrowingRecord currentRecord = transactionService.getCurrentBorrowingRecord(isbn);
        
        if (currentRecord == null) {
            System.out.println("Status: Available in library");
        } else {
            User borrower = authService.getUserByEmail(currentRecord.getBorrowerEmail());
            if (borrower != null) {
                System.out.println("Status: Currently borrowed");
                System.out.println("Borrowed by: " + borrower.getName() + " (" + borrower.getEmail() + ")");
                System.out.println("Borrowed date: " + currentRecord.getBorrowDate().format(DATE_FORMAT));
                System.out.println("Due date: " + currentRecord.getDueDate().format(DATE_FORMAT));
                System.out.println("Expected return: " + currentRecord.getDueDate().format(DATE_FORMAT));
            }
        }
    }
    
    public void generateAllFinesReport() {
        System.out.println("\n--- All Fines Report ---");
        List<FineRecord> allFines = transactionService.getAllFineRecords();
        
        if (allFines.isEmpty()) {
            System.out.println("No fines recorded.");
            return;
        }
        
        List<FineRecord> sortedFines = new ArrayList<>(allFines);
        sortedFines.sort((f1, f2) -> f2.getFineDate().compareTo(f1.getFineDate()));
        
        for (FineRecord fine : sortedFines) {
            User borrower = authService.getUserByEmail(fine.getBorrowerEmail());
            String bookTitle = fine.getIsbn().equals("CARD") ? "Membership Card" : 
                bookService.getBookByIsbn(fine.getIsbn()).getTitle();
            
            if (borrower != null) {
                System.out.println(borrower.getName() + " - " + bookTitle + " - Rs. " + 
                    fine.getAmount() + " (" + fine.getReason() + ") - " + 
                    (fine.isPaid() ? "PAID" : "UNPAID"));
            }
        }
    }
    
    public void generateUnpaidFinesReport() {
        System.out.println("\n--- All Unpaid Fines ---");
        List<FineRecord> unpaidFines = transactionService.getAllUnpaidFines();
        
        if (unpaidFines.isEmpty()) {
            System.out.println("No unpaid fines.");
            return;
        }
        
        double totalUnpaid = 0;
        for (FineRecord fine : unpaidFines) {
            User borrower = authService.getUserByEmail(fine.getBorrowerEmail());
            String bookTitle = fine.getIsbn().equals("CARD") ? "Membership Card" : 
                bookService.getBookByIsbn(fine.getIsbn()).getTitle();
            
            if (borrower != null) {
                System.out.println(borrower.getName() + " (" + borrower.getEmail() + ") - " + 
                    bookTitle + " - Rs. " + fine.getAmount() + " (" + fine.getReason() + ")");
                totalUnpaid += fine.getAmount();
            }
        }
        System.out.println("\nTotal unpaid amount: Rs. " + totalUnpaid);
    }
    
    // Borrower Reports
    public void generateBorrowerFineHistory(String borrowerEmail) {
        System.out.println("\n--- My Fine History ---");
        List<FineRecord> fineHistory = transactionService.getFineHistory(borrowerEmail);
        
        if (fineHistory.isEmpty()) {
            System.out.println("No fines recorded.");
            return;
        }
        
        double totalUnpaid = 0;
        for (FineRecord fine : fineHistory) {
            String bookTitle = fine.getIsbn().equals("CARD") ? "Membership Card" : 
                bookService.getBookByIsbn(fine.getIsbn()).getTitle();
            
            System.out.println(fine.getFineDate().format(DATE_FORMAT) + " - " + bookTitle + 
                " - Rs. " + fine.getAmount() + " (" + fine.getReason() + ") - " + 
                (fine.isPaid() ? "PAID" : "UNPAID"));
            
            if (!fine.isPaid()) {
                totalUnpaid += fine.getAmount();
            }
        }
        
        System.out.println("\nTotal unpaid fines: Rs. " + totalUnpaid);
        
        // Show account balance
        User user = authService.getUserByEmail(borrowerEmail);
        if (user != null) {
            System.out.println("Account balance: Rs. " + user.getAccountBalance());
        }
    }
    
    public void generateBorrowerBorrowingHistory(String borrowerEmail) {
        System.out.println("\n--- My Borrowing History ---");
        List<BorrowingRecord> borrowingHistory = transactionService.getBorrowingHistory(borrowerEmail);
        
        if (borrowingHistory.isEmpty()) {
            System.out.println("No borrowing history.");
            return;
        }
        
        for (BorrowingRecord record : borrowingHistory) {
            Book book = bookService.getBookByIsbn(record.getIsbn());
            if (book != null) {
                String status = record.getReturnDate() == null ? "CURRENTLY BORROWED" : "RETURNED";
                String returnInfo = record.getReturnDate() == null ? 
                    "Due: " + record.getDueDate().format(DATE_FORMAT) :
                    "Returned: " + record.getReturnDate().format(DATE_FORMAT);
                
                System.out.println(book.getTitle() + " - Borrowed: " + 
                    record.getBorrowDate().format(DATE_FORMAT) + " - " + returnInfo + " - " + status);
            }
        }
    }
}
