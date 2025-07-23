package LibrarySystem.transactions;

import LibrarySystem.models.*;
import LibrarySystem.books.BookService;
import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TransactionService {
    private static final String BORROWING_FILE = "borrowing.txt";
    private static final String FINES_FILE = "fines.txt";
    
    private List<BorrowingRecord> borrowingRecords;
    private List<FineRecord> fineRecords;
    private BookService bookService;
    
    public TransactionService(BookService bookService) {
        this.bookService = bookService;
        this.borrowingRecords = new ArrayList<>();
        this.fineRecords = new ArrayList<>();
        loadBorrowingRecords();
        loadFineRecords();
    }
    
    public BorrowResult borrowBookWithChecks(String borrowerEmail, String isbn, LibrarySystem.auth.AuthService authService) {
        List<BorrowingRecord> currentBorrowedBooks = getCurrentBorrowedBooks(borrowerEmail);
        
        // Check if user already has 3 books
        if (currentBorrowedBooks.size() >= 3) {
            return BorrowResult.MAX_BOOKS_REACHED;
        }
        
        // Check if book is available
        Book book = bookService.getBookByIsbn(isbn);
        if (book == null || book.getAvailableQuantity() <= 0) {
            return BorrowResult.BOOK_NOT_AVAILABLE;
        }
        
        // Check if user already borrowed this book
        for (BorrowingRecord record : currentBorrowedBooks) {
            if (record.getIsbn().equals(isbn)) {
                return BorrowResult.ALREADY_BORROWED;
            }
        }
        
        // Check for unpaid fines
        double unpaidFines = getTotalUnpaidFines(borrowerEmail);
        if (unpaidFines > 0) {
            return BorrowResult.HAS_UNPAID_FINES;
        }
        
        // Create borrowing record
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(15);
        
        BorrowingRecord record = new BorrowingRecord(borrowerEmail, isbn, borrowDate, dueDate);
        borrowingRecords.add(record);
        
        // Update book quantity
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        
        return BorrowResult.SUCCESS;
    }
    
    public boolean borrowBook(String borrowerEmail, String isbn) {
        List<BorrowingRecord> currentBorrowedBooks = getCurrentBorrowedBooks(borrowerEmail);
        
        // Check if user already has 3 books
        if (currentBorrowedBooks.size() >= 3) {
            return false;
        }
        
        // Check if book is available
        Book book = bookService.getBookByIsbn(isbn);
        if (book == null || book.getAvailableQuantity() <= 0) {
            return false;
        }
        
        // Check if user already borrowed this book
        for (BorrowingRecord record : currentBorrowedBooks) {
            if (record.getIsbn().equals(isbn)) {
                return false; // Already borrowed
            }
        }
        
        // Create borrowing record
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(15);
        
        BorrowingRecord record = new BorrowingRecord(borrowerEmail, isbn, borrowDate, dueDate);
        borrowingRecords.add(record);
        
        // Update book quantity
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        
        return true;
    }
    
    public boolean returnBook(String borrowerEmail, String isbn, LocalDate returnDate) {
        BorrowingRecord record = null;
        
        // Find the borrowing record
        for (BorrowingRecord br : borrowingRecords) {
            if (br.getBorrowerEmail().equals(borrowerEmail) && 
                br.getIsbn().equals(isbn) && br.getReturnDate() == null) {
                record = br;
                break;
            }
        }
        
        if (record == null) {
            return false; // No active borrowing record found
        }
        
        // Mark as returned
        record.setReturnDate(returnDate);
        
        // Update book quantity
        Book book = bookService.getBookByIsbn(isbn);
        if (book != null) {
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        }
        
        // Calculate fine if overdue
        long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
        if (daysOverdue > 0) {
            double fine = calculateOverdueFine(daysOverdue, book.getCost());
            FineRecord fineRecord = new FineRecord(
                borrowerEmail, isbn, fine, FineReason.OVERDUE, LocalDate.now()
            );
            fineRecords.add(fineRecord);
            return true; // Book returned but with fine
        }
        
        return true; // Book returned without fine
    }
    
    public boolean extendBookTenure(String borrowerEmail, String isbn) {
        BorrowingRecord record = null;
        
        // Find the borrowing record
        for (BorrowingRecord br : borrowingRecords) {
            if (br.getBorrowerEmail().equals(borrowerEmail) && 
                br.getIsbn().equals(isbn) && br.getReturnDate() == null) {
                record = br;
                break;
            }
        }
        
        if (record == null || record.getExtensions() >= 2) {
            return false; // No record found or max extensions reached
        }
        
        record.setDueDate(record.getDueDate().plusDays(15));
        record.setExtensions(record.getExtensions() + 1);
        return true;
    }
    
    public void reportLostBook(String borrowerEmail, String isbn) {
        // Find and mark the borrowing record as returned (lost)
        for (BorrowingRecord record : borrowingRecords) {
            if (record.getBorrowerEmail().equals(borrowerEmail) && 
                record.getIsbn().equals(isbn) && record.getReturnDate() == null) {
                record.setReturnDate(LocalDate.now());
                break;
            }
        }
        
        // Add fine for lost book
        Book book = bookService.getBookByIsbn(isbn);
        if (book != null) {
            double fine = book.getCost() * 0.5; // 50% of book cost
            FineRecord fineRecord = new FineRecord(
                borrowerEmail, isbn, fine, FineReason.LOST_BOOK, LocalDate.now()
            );
            fineRecords.add(fineRecord);
        }
    }
    
    public void reportLostCard(String borrowerEmail) {
        double fine = 10.0; // Rs. 10 for lost card
        FineRecord fineRecord = new FineRecord(
            borrowerEmail, "CARD", fine, FineReason.LOST_CARD, LocalDate.now()
        );
        fineRecords.add(fineRecord);
    }
    
    public List<BorrowingRecord> getCurrentBorrowedBooks(String borrowerEmail) {
        List<BorrowingRecord> result = new ArrayList<>();
        for (BorrowingRecord record : borrowingRecords) {
            if (record.getBorrowerEmail().equals(borrowerEmail) && record.getReturnDate() == null) {
                result.add(record);
            }
        }
        return result;
    }
    
    public List<BorrowingRecord> getBorrowingHistory(String borrowerEmail) {
        List<BorrowingRecord> result = new ArrayList<>();
        for (BorrowingRecord record : borrowingRecords) {
            if (record.getBorrowerEmail().equals(borrowerEmail)) {
                result.add(record);
            }
        }
        // Sort by borrow date (newest first)
        result.sort((r1, r2) -> r2.getBorrowDate().compareTo(r1.getBorrowDate()));
        return result;
    }
    
    public List<FineRecord> getFineHistory(String borrowerEmail) {
        List<FineRecord> result = new ArrayList<>();
        for (FineRecord fine : fineRecords) {
            if (fine.getBorrowerEmail().equals(borrowerEmail)) {
                result.add(fine);
            }
        }
        // Sort by date (newest first)
        result.sort((f1, f2) -> f2.getFineDate().compareTo(f1.getFineDate()));
        return result;
    }
    
    public List<FineRecord> getAllUnpaidFines() {
        List<FineRecord> result = new ArrayList<>();
        for (FineRecord fine : fineRecords) {
            if (!fine.isPaid()) {
                result.add(fine);
            }
        }
        result.sort((f1, f2) -> f2.getFineDate().compareTo(f1.getFineDate()));
        return result;
    }
    
    public List<BorrowingRecord> getOutstandingBooks(LocalDate checkDate) {
        List<BorrowingRecord> result = new ArrayList<>();
        for (BorrowingRecord record : borrowingRecords) {
            if (record.getReturnDate() == null && record.getDueDate().isBefore(checkDate)) {
                result.add(record);
            }
        }
        return result;
    }
    
    public BorrowingRecord getCurrentBorrowingRecord(String isbn) {
        for (BorrowingRecord record : borrowingRecords) {
            if (record.getIsbn().equals(isbn) && record.getReturnDate() == null) {
                return record;
            }
        }
        return null;
    }
    
    public Map<String, Long> getMostBorrowedBooks() {
        Map<String, Long> borrowCounts = new HashMap<>();
        for (BorrowingRecord record : borrowingRecords) {
            borrowCounts.merge(record.getIsbn(), 1L, Long::sum);
        }
        return borrowCounts;
    }
    
    public Set<String> getNeverBorrowedBooks() {
        Set<String> borrowedIsbns = new HashSet<>();
        for (BorrowingRecord record : borrowingRecords) {
            borrowedIsbns.add(record.getIsbn());
        }
        
        Set<String> neverBorrowed = new HashSet<>();
        for (Book book : bookService.getAllBooks()) {
            if (!borrowedIsbns.contains(book.getIsbn())) {
                neverBorrowed.add(book.getIsbn());
            }
        }
        return neverBorrowed;
    }
    
    private double calculateOverdueFine(long daysOverdue, double bookCost) {
        double fine = daysOverdue * 2.0; // Rs. 2 per day
        
        // Exponential increase for every 10 days
        long periods = daysOverdue / 10;
        for (int i = 0; i < periods; i++) {
            fine *= 2;
        }
        
        // Cap at 80% of book cost
        double maxFine = bookCost * 0.8;
        return Math.min(fine, maxFine);
    }
    
    public void saveData() {
        saveBorrowingRecords();
        saveFineRecords();
    }
    
    private void loadBorrowingRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BORROWING_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    BorrowingRecord record = new BorrowingRecord(
                        parts[0], parts[1], 
                        LocalDate.parse(parts[2]), 
                        LocalDate.parse(parts[3])
                    );
                    if (parts.length > 4 && !parts[4].equals("null")) {
                        record.setReturnDate(LocalDate.parse(parts[4]));
                    }
                    if (parts.length > 5) {
                        record.setExtensions(Integer.parseInt(parts[5]));
                    }
                    borrowingRecords.add(record);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, which is fine for first run
        }
    }
    
    private void saveBorrowingRecords() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BORROWING_FILE))) {
            for (BorrowingRecord record : borrowingRecords) {
                writer.println(record.getBorrowerEmail() + "|" + record.getIsbn() + "|" + 
                    record.getBorrowDate() + "|" + record.getDueDate() + "|" + 
                    record.getReturnDate() + "|" + record.getExtensions());
            }
        } catch (IOException e) {
            System.err.println("Error saving borrowing records: " + e.getMessage());
        }
    }
    
    private void loadFineRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FINES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    FineRecord record = new FineRecord(
                        parts[0], parts[1], Double.parseDouble(parts[2]), 
                        FineReason.valueOf(parts[3]), LocalDate.parse(parts[4])
                    );
                    record.setPaid(Boolean.parseBoolean(parts[5]));
                    fineRecords.add(record);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, which is fine for first run
        }
    }
    
    private void saveFineRecords() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FINES_FILE))) {
            for (FineRecord record : fineRecords) {
                writer.println(record.getBorrowerEmail() + "|" + record.getIsbn() + "|" + 
                    record.getAmount() + "|" + record.getReason() + "|" + 
                    record.getFineDate() + "|" + record.isPaid());
            }
        } catch (IOException e) {
            System.err.println("Error saving fine records: " + e.getMessage());
        }
    }
    
    public List<FineRecord> getAllFineRecords() {
        return fineRecords;
    }
    
    // Payment Methods
    public boolean payFineWithCash(String borrowerEmail, String isbn, FineReason reason) {
        FineRecord fine = findUnpaidFine(borrowerEmail, isbn, reason);
        if (fine != null) {
            fine.setPaid(true);
            return true;
        }
        return false;
    }
    
    public boolean payFineWithAccount(String borrowerEmail, String isbn, FineReason reason, LibrarySystem.auth.AuthService authService) {
        FineRecord fine = findUnpaidFine(borrowerEmail, isbn, reason);
        if (fine != null) {
            LibrarySystem.models.User user = authService.getUserByEmail(borrowerEmail);
            if (user != null && user.deductFromAccountBalance(fine.getAmount())) {
                fine.setPaid(true);
                return true;
            }
        }
        return false;
    }
    
    public boolean payAllUnpaidFinesWithCash(String borrowerEmail) {
        List<FineRecord> unpaidFines = new ArrayList<>();
        for (FineRecord fine : fineRecords) {
            if (fine.getBorrowerEmail().equals(borrowerEmail) && !fine.isPaid()) {
                unpaidFines.add(fine);
            }
        }
        
        for (FineRecord fine : unpaidFines) {
            fine.setPaid(true);
        }
        return !unpaidFines.isEmpty();
    }
    
    public boolean payAllUnpaidFinesWithAccount(String borrowerEmail, LibrarySystem.auth.AuthService authService) {
        List<FineRecord> unpaidFines = new ArrayList<>();
        double totalAmount = 0;
        
        for (FineRecord fine : fineRecords) {
            if (fine.getBorrowerEmail().equals(borrowerEmail) && !fine.isPaid()) {
                unpaidFines.add(fine);
                totalAmount += fine.getAmount();
            }
        }
        
        if (unpaidFines.isEmpty()) return false;
        
        LibrarySystem.models.User user = authService.getUserByEmail(borrowerEmail);
        if (user != null && user.deductFromAccountBalance(totalAmount)) {
            for (FineRecord fine : unpaidFines) {
                fine.setPaid(true);
            }
            return true;
        }
        return false;
    }
    
    public double getTotalUnpaidFines(String borrowerEmail) {
        double total = 0;
        for (FineRecord fine : fineRecords) {
            if (fine.getBorrowerEmail().equals(borrowerEmail) && !fine.isPaid()) {
                total += fine.getAmount();
            }
        }
        return total;
    }
    
    private FineRecord findUnpaidFine(String borrowerEmail, String isbn, FineReason reason) {
        for (FineRecord fine : fineRecords) {
            if (fine.getBorrowerEmail().equals(borrowerEmail) && 
                fine.getIsbn().equals(isbn) && 
                fine.getReason() == reason && 
                !fine.isPaid()) {
                return fine;
            }
        }
        return null;
    }
}
