package LibrarySystem;

import LibrarySystem.models.*;
import LibrarySystem.auth.AuthService;
import LibrarySystem.books.BookService;
import LibrarySystem.transactions.TransactionService;
import LibrarySystem.reports.ReportService;
import LibrarySystem.users.*;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private AuthService authService;
    private BookService bookService;
    private TransactionService transactionService;
    private ReportService reportService;
    private Scanner scanner;
    
    public Main() {
        this.authService = new AuthService();
        this.bookService = new BookService();
        this.transactionService = new TransactionService(bookService);
        this.reportService = new ReportService(bookService, transactionService, authService);
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    public void start() {
        System.out.println("==============================================");
        System.out.println("   Welcome to Library Management System");
        System.out.println("==============================================");
        
        User currentUser = authenticate();
        if (currentUser != null) {
            if (currentUser.getRole() == UserRole.ADMIN) {
                Admin admin = new Admin(currentUser);
                adminMenu(admin);
            } else {
                Borrower borrower = new Borrower(currentUser);
                borrowerMenu(borrower);
            }
        }
        
        saveAllData();
        scanner.close();
    }
    
    private User authenticate() {
        System.out.println("\n--- Authentication ---");
        System.out.print("Enter Email ID: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        
        User user = authService.authenticate(email, password);
        if (user != null) {
            System.out.println("\nAuthentication successful!");
            System.out.println("Welcome, " + user.getName() + "!");
            return user;
        } else {
            System.out.println("Invalid credentials. Access denied.");
            return null;
        }
    }
    
    private void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Book Inventory Management");
            System.out.println("2. User Management");
            System.out.println("3. Reports");
            System.out.println("4. Fine Management");
            System.out.println("5. Logout");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    bookInventoryMenu();
                    break;
                case 2:
                    userManagementMenu();
                    break;
                case 3:
                    adminReportsMenu();
                    break;
                case 4:
                    fineManagementMenu();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void borrowerMenu(Borrower borrower) {
        while (true) {
            User user = borrower.getUser();
            System.out.println("\n=== BORROWER MENU ===");
            System.out.println("Current Security Deposit: Rs. " + user.getSecurityDeposit());
            System.out.println("Account Balance: Rs. " + user.getAccountBalance());
            System.out.println("Books Currently Borrowed: " + 
                transactionService.getCurrentBorrowedBooks(user.getEmail()).size() + "/3");
            double unpaidFines = transactionService.getTotalUnpaidFines(user.getEmail());
            if (unpaidFines > 0) {
                System.out.println("Unpaid Fines: Rs. " + unpaidFines);
            }
            System.out.println();
            System.out.println("1. View Available Books");
            System.out.println("2. Search Books");
            System.out.println("3. Borrow Books");
            System.out.println("4. Return Books");
            System.out.println("5. Extend Book Tenure");
            System.out.println("6. Report Lost Book/Card");
            System.out.println("7. View My Reports");
            System.out.println("8. Account Management");
            System.out.println("9. Logout");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    viewAvailableBooks();
                    break;
                case 2:
                    searchBooksMenu();
                    break;
                case 3:
                    borrowBooksMenu(borrower);
                    break;
                case 4:
                    returnBooksMenu(borrower);
                    break;
                case 5:
                    extendTenureMenu(borrower);
                    break;
                case 6:
                    reportLostMenu(borrower);
                    break;
                case 7:
                    borrowerReportsMenu(borrower);
                    break;
                case 8:
                    accountManagementMenu(borrower);
                    break;
                case 9:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    // Book Inventory Management Methods
    private void bookInventoryMenu() {
        while (true) {
            System.out.println("\n=== BOOK INVENTORY MANAGEMENT ===");
            System.out.println("1. Add Book");
            System.out.println("2. Modify Book Details");
            System.out.println("3. Delete Book");
            System.out.println("4. View All Books (Sorted by Name)");
            System.out.println("5. View All Books (Sorted by Quantity)");
            System.out.println("6. Search Book");
            System.out.println("7. Back to Main Menu");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    modifyBook();
                    break;
                case 3:
                    deleteBook();
                    break;
                case 4:
                    viewBooksSortedByName();
                    break;
                case 5:
                    viewBooksSortedByQuantity();
                    break;
                case 6:
                    searchBooksMenu();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void addBook() {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        
        if (bookService.getBookByIsbn(isbn) != null) {
            System.out.println("Book with this ISBN already exists!");
            return;
        }
        
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();
        
        System.out.print("Enter Available Quantity: ");
        int quantity = getIntInput();
        
        System.out.print("Enter Book Cost (Rs): ");
        double cost = getDoubleInput();
        
        if (bookService.addBook(isbn, title, author, quantity, cost)) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Failed to add book.");
        }
    }
    
    private void modifyBook() {
        System.out.println("\n--- Modify Book Details ---");
        System.out.print("Enter ISBN of book to modify: ");
        String isbn = scanner.nextLine().trim();
        
        Book book = bookService.getBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        System.out.println("Current Details: " + book);
        System.out.println("1. Modify Title");
        System.out.println("2. Modify Author");
        System.out.println("3. Modify Available Quantity");
        System.out.println("4. Modify Cost");
        System.out.print("Select option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                System.out.print("Enter new title: ");
                book.setTitle(scanner.nextLine().trim());
                break;
            case 2:
                System.out.print("Enter new author: ");
                book.setAuthor(scanner.nextLine().trim());
                break;
            case 3:
                System.out.print("Enter new quantity: ");
                book.setAvailableQuantity(getIntInput());
                break;
            case 4:
                System.out.print("Enter new cost: ");
                book.setCost(getDoubleInput());
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        
        System.out.println("Book details updated successfully!");
    }
    
    private void deleteBook() {
        System.out.println("\n--- Delete Book ---");
        System.out.print("Enter ISBN of book to delete: ");
        String isbn = scanner.nextLine().trim();
        
        Book book = bookService.getBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        // Check if book is currently borrowed
        BorrowingRecord currentRecord = transactionService.getCurrentBorrowingRecord(isbn);
        if (currentRecord != null) {
            System.out.println("Cannot delete book. It is currently borrowed by someone.");
            return;
        }
        
        System.out.println("Book to delete: " + book);
        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            if (bookService.deleteBook(isbn)) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Failed to delete book.");
            }
        }
    }
    
    private void viewBooksSortedByName() {
        System.out.println("\n--- Books Sorted by Name ---");
        List<Book> books = bookService.getBooksSortedByTitle();
        for (Book book : books) {
            System.out.println(book);
        }
    }
    
    private void viewBooksSortedByQuantity() {
        System.out.println("\n--- Books Sorted by Available Quantity ---");
        List<Book> books = bookService.getBooksSortedByQuantity();
        for (Book book : books) {
            System.out.println(book);
        }
    }
    
    private void searchBooksMenu() {
        System.out.println("\n--- Search Books ---");
        System.out.println("1. Search by Title");
        System.out.println("2. Search by ISBN");
        System.out.println("3. Search by Author");
        System.out.print("Select option: ");
        
        int choice = getIntInput();
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine().trim();
        
        List<Book> results = new ArrayList<>();
        
        switch (choice) {
            case 1:
                results = bookService.searchBooksByTitle(searchTerm);
                break;
            case 2:
                Book book = bookService.getBookByIsbn(searchTerm.toUpperCase());
                if (book != null) results.add(book);
                break;
            case 3:
                results = bookService.searchBooksByAuthor(searchTerm);
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        
        if (results.isEmpty()) {
            System.out.println("No books found matching your search.");
        } else {
            System.out.println("\n--- Search Results ---");
            for (Book result : results) {
                System.out.println(result);
            }
        }
    }
    
    // User Management Methods
    private void userManagementMenu() {
        while (true) {
            System.out.println("\n=== USER MANAGEMENT ===");
            System.out.println("1. Add Admin");
            System.out.println("2. Add Borrower");
            System.out.println("3. View All Users");
            System.out.println("4. Modify User");
            System.out.println("5. Promote User to Admin");
            System.out.println("6. Delete User");
            System.out.println("7. Add Money to User Account");
            System.out.println("8. Back to Main Menu");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    addUser(UserRole.ADMIN);
                    break;
                case 2:
                    addUser(UserRole.BORROWER);
                    break;
                case 3:
                    viewAllUsers();
                    break;
                case 4:
                    modifyUser();
                    break;
                case 5:
                    promoteUserToAdmin();
                    break;
                case 6:
                    deleteUser();
                    break;
                case 7:
                    addMoneyToUserAccount();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void addUser(UserRole role) {
        System.out.println("\n--- Add " + role + " ---");
        System.out.print("Enter Email ID: ");
        String email = scanner.nextLine().trim();
        
        if (authService.getUserByEmail(email) != null) {
            System.out.println("User with this email already exists!");
            return;
        }
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        
        double securityDeposit = 0;
        if (role == UserRole.BORROWER) {
            securityDeposit = 1500.0; // Initial deposit
            System.out.println("Initial security deposit of Rs. 1500 will be collected.");
        }
        
        if (authService.registerUser(email, name, password, role, securityDeposit)) {
            System.out.println(role + " added successfully!");
        } else {
            System.out.println("Failed to add user.");
        }
    }
    
    private void viewAllUsers() {
        System.out.println("\n--- All Users ---");
        Map<String, User> users = authService.getAllUsers();
        for (User user : users.values()) {
            System.out.println(user);
        }
    }
    
    private void modifyUser() {
        System.out.println("\n--- Modify User ---");
        System.out.print("Enter Email ID of user to modify: ");
        String email = scanner.nextLine().trim();
        
        User user = authService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        
        System.out.println("Current Details: " + user);
        System.out.println("1. Modify Name");
        System.out.println("2. Modify Password");
        System.out.println("3. Modify Security Deposit (Borrowers only)");
        System.out.print("Select option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                System.out.print("Enter new name: ");
                user.setName(scanner.nextLine().trim());
                break;
            case 2:
                System.out.print("Enter new password: ");
                user.setPassword(scanner.nextLine().trim());
                break;
            case 3:
                if (user.getRole() == UserRole.BORROWER) {
                    System.out.print("Enter new security deposit: ");
                    user.setSecurityDeposit(getDoubleInput());
                } else {
                    System.out.println("Security deposit only applicable for borrowers.");
                }
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        
        System.out.println("User details updated successfully!");
    }
    
    // Borrowing Methods
    private void borrowBooksMenu(Borrower borrower) {
        if (!borrower.canBorrowBooks()) {
            System.out.println("Cannot borrow books. Either insufficient security deposit (min Rs. 500) or invalid role.");
            return;
        }
        
        List<BorrowingRecord> currentBorrowedBooks = transactionService.getCurrentBorrowedBooks(borrower.getUser().getEmail());
        if (currentBorrowedBooks.size() >= 3) {
            System.out.println("You have already borrowed the maximum number of books (3).");
            return;
        }
        
        System.out.print("Enter ISBN or Book Title to borrow: ");
        String searchTerm = scanner.nextLine().trim();
        
        Book book = bookService.findBook(searchTerm);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        BorrowResult result = transactionService.borrowBookWithChecks(borrower.getUser().getEmail(), book.getIsbn(), authService);
        
        switch (result) {
            case SUCCESS:
                System.out.println("Book borrowed successfully!");
                System.out.println("Book: " + book.getTitle());
                System.out.println("Due date: " + LocalDate.now().plusDays(15).format(DATE_FORMAT));
                break;
            case MAX_BOOKS_REACHED:
                System.out.println("You have already borrowed the maximum number of books (3).");
                break;
            case BOOK_NOT_AVAILABLE:
                System.out.println("Book is not available for borrowing.");
                break;
            case ALREADY_BORROWED:
                System.out.println("You have already borrowed this book.");
                break;
            case HAS_UNPAID_FINES:
                System.out.println("You have unpaid fines. Please pay them before borrowing books.");
                double unpaidAmount = transactionService.getTotalUnpaidFines(borrower.getUser().getEmail());
                System.out.println("Total unpaid fines: Rs. " + unpaidAmount);
                System.out.println("Account balance: Rs. " + borrower.getUser().getAccountBalance());
                
                if (borrower.getUser().getAccountBalance() >= unpaidAmount) {
                    System.out.print("Would you like to pay fines from your account? (y/n): ");
                    String choice = scanner.nextLine().trim().toLowerCase();
                    if (choice.equals("y")) {
                        if (transactionService.payAllUnpaidFinesWithAccount(borrower.getUser().getEmail(), authService)) {
                            System.out.println("All fines paid successfully from account!");
                            // Try borrowing again
                            if (transactionService.borrowBook(borrower.getUser().getEmail(), book.getIsbn())) {
                                System.out.println("Book borrowed successfully!");
                                System.out.println("Book: " + book.getTitle());
                                System.out.println("Due date: " + LocalDate.now().plusDays(15).format(DATE_FORMAT));
                            }
                        } else {
                            System.out.println("Payment failed. Please try again.");
                        }
                    }
                } else {
                    System.out.print("Would you like to add money to your account? (y/n): ");
                    String choice = scanner.nextLine().trim().toLowerCase();
                    if (choice.equals("y")) {
                        addMoneyToAccount(borrower);
                    }
                }
                break;
            default:
                System.out.println("Failed to borrow book.");
        }
    }
    
    private void returnBooksMenu(Borrower borrower) {
        List<BorrowingRecord> borrowedBooks = transactionService.getCurrentBorrowedBooks(borrower.getUser().getEmail());
        
        if (borrowedBooks.isEmpty()) {
            System.out.println("You have no books to return.");
            return;
        }
        
        System.out.println("\n--- Your Borrowed Books ---");
        for (int i = 0; i < borrowedBooks.size(); i++) {
            BorrowingRecord record = borrowedBooks.get(i);
            Book book = bookService.getBookByIsbn(record.getIsbn());
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
            
            System.out.println((i + 1) + ". " + book.getTitle() + 
                " (Due: " + record.getDueDate().format(DATE_FORMAT) + 
                (daysOverdue > 0 ? ", OVERDUE by " + daysOverdue + " days" : "") + ")");
        }
        
        System.out.print("Enter book number to return: ");
        int bookNum = getIntInput();
        
        if (bookNum >= 1 && bookNum <= borrowedBooks.size()) {
            BorrowingRecord record = borrowedBooks.get(bookNum - 1);
            
            System.out.print("Enter return date (DD/MM/YYYY): ");
            String dateStr = scanner.nextLine().trim();
            
            LocalDate returnDate;
            try {
                returnDate = LocalDate.parse(dateStr, DATE_FORMAT);
            } catch (Exception e) {
                System.out.println("Invalid date format!");
                return;
            }
            
            if (transactionService.returnBook(record.getBorrowerEmail(), record.getIsbn(), returnDate)) {
                System.out.println("Book returned successfully!");
            } else {
                System.out.println("Failed to return book.");
            }
        } else {
            System.out.println("Invalid book number.");
        }
    }
    
    private void extendTenureMenu(Borrower borrower) {
        List<BorrowingRecord> borrowedBooks = transactionService.getCurrentBorrowedBooks(borrower.getUser().getEmail());
        
        if (borrowedBooks.isEmpty()) {
            System.out.println("You have no books to extend.");
            return;
        }
        
        System.out.println("\n--- Your Borrowed Books ---");
        for (int i = 0; i < borrowedBooks.size(); i++) {
            BorrowingRecord record = borrowedBooks.get(i);
            Book book = bookService.getBookByIsbn(record.getIsbn());
            System.out.println((i + 1) + ". " + book.getTitle() + 
                " (Due: " + record.getDueDate().format(DATE_FORMAT) + 
                ", Extensions: " + record.getExtensions() + "/2)");
        }
        
        System.out.print("Enter book number to extend: ");
        int bookNum = getIntInput();
        
        if (bookNum >= 1 && bookNum <= borrowedBooks.size()) {
            BorrowingRecord record = borrowedBooks.get(bookNum - 1);
            
            if (transactionService.extendBookTenure(record.getBorrowerEmail(), record.getIsbn())) {
                System.out.println("Book tenure extended successfully!");
                System.out.println("New due date: " + record.getDueDate().format(DATE_FORMAT));
            } else {
                System.out.println("Failed to extend tenure. Maximum extensions (2) may have been reached.");
            }
        } else {
            System.out.println("Invalid book number.");
        }
    }
    
    private void reportLostMenu(Borrower borrower) {
        System.out.println("\n--- Report Lost ---");
        System.out.println("1. Report Lost Book");
        System.out.println("2. Report Lost Membership Card");
        System.out.print("Select option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                reportLostBook(borrower);
                break;
            case 2:
                transactionService.reportLostCard(borrower.getUser().getEmail());
                System.out.println("Membership card reported as lost. Fine of Rs. 10 has been applied.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void reportLostBook(Borrower borrower) {
        List<BorrowingRecord> borrowedBooks = transactionService.getCurrentBorrowedBooks(borrower.getUser().getEmail());
        
        if (borrowedBooks.isEmpty()) {
            System.out.println("You have no borrowed books to report as lost.");
            return;
        }
        
        System.out.println("\n--- Your Borrowed Books ---");
        for (int i = 0; i < borrowedBooks.size(); i++) {
            BorrowingRecord record = borrowedBooks.get(i);
            Book book = bookService.getBookByIsbn(record.getIsbn());
            System.out.println((i + 1) + ". " + book.getTitle());
        }
        
        System.out.print("Enter book number to report as lost: ");
        int bookNum = getIntInput();
        
        if (bookNum >= 1 && bookNum <= borrowedBooks.size()) {
            BorrowingRecord record = borrowedBooks.get(bookNum - 1);
            Book book = bookService.getBookByIsbn(record.getIsbn());
            
            transactionService.reportLostBook(record.getBorrowerEmail(), record.getIsbn());
            
            double fine = book.getCost() * 0.5; // 50% of book cost
            System.out.println("Book reported as lost.");
            System.out.println("Fine amount: Rs. " + fine + " (50% of book cost)");
        } else {
            System.out.println("Invalid book number.");
        }
    }
    
    // Reports Methods
    private void adminReportsMenu() {
        while (true) {
            System.out.println("\n=== ADMIN REPORTS ===");
            System.out.println("1. Books with Low Quantity");
            System.out.println("2. Books Never Borrowed");
            System.out.println("3. Most Borrowed Books");
            System.out.println("4. Outstanding Books Report");
            System.out.println("5. Book Status by ISBN");
            System.out.println("6. All Fines Report");
            System.out.println("7. Back to Main Menu");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    System.out.print("Enter minimum quantity threshold: ");
                    int threshold = getIntInput();
                    reportService.generateLowQuantityBooksReport(threshold);
                    break;
                case 2:
                    reportService.generateNeverBorrowedBooksReport();
                    break;
                case 3:
                    reportService.generateMostBorrowedBooksReport();
                    break;
                case 4:
                    System.out.print("Enter date to check outstanding books (DD/MM/YYYY): ");
                    String dateStr = scanner.nextLine().trim();
                    try {
                        LocalDate checkDate = LocalDate.parse(dateStr, DATE_FORMAT);
                        reportService.generateOutstandingBooksReport(checkDate);
                    } catch (Exception e) {
                        System.out.println("Invalid date format!");
                    }
                    break;
                case 5:
                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine().trim();
                    reportService.generateBookStatusReport(isbn);
                    break;
                case 6:
                    reportService.generateAllFinesReport();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void borrowerReportsMenu(Borrower borrower) {
        while (true) {
            System.out.println("\n=== MY REPORTS ===");
            System.out.println("1. My Fine History");
            System.out.println("2. My Borrowing History");
            System.out.println("3. Pay Fines");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    reportService.generateBorrowerFineHistory(borrower.getUser().getEmail());
                    break;
                case 2:
                    reportService.generateBorrowerBorrowingHistory(borrower.getUser().getEmail());
                    break;
                case 3:
                    payFinesMenu(borrower);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void fineManagementMenu() {
        System.out.println("\n=== FINE MANAGEMENT ===");
        System.out.println("1. View Unpaid Fines");
        System.out.println("2. Mark Fine as Paid (Cash)");
        System.out.println("3. Process Payment from Account");
        System.out.print("Select option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                reportService.generateUnpaidFinesReport();
                break;
            case 2:
                markFineAsPaidCash();
                break;
            case 3:
                processAccountPayment();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void viewAvailableBooks() {
        System.out.println("\n--- Available Books ---");
        List<Book> availableBooks = bookService.getAvailableBooks();
        for (Book book : availableBooks) {
            System.out.println(book);
        }
    }
    
    // Utility Methods
    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    private double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    private void saveAllData() {
        authService.saveUsers();
        bookService.saveBooks();
        transactionService.saveData();
    }
    
    // Account Management Methods
    private void accountManagementMenu(Borrower borrower) {
        while (true) {
            User user = borrower.getUser();
            System.out.println("\n=== ACCOUNT MANAGEMENT ===");
            System.out.println("Current Account Balance: Rs. " + user.getAccountBalance());
            System.out.println("Security Deposit: Rs. " + user.getSecurityDeposit());
            System.out.println();
            System.out.println("1. Add Money to Account");
            System.out.println("2. View Account Details");
            System.out.println("3. Back to Main Menu");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    addMoneyToAccount(borrower);
                    break;
                case 2:
                    viewAccountDetails(borrower);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void addMoneyToAccount(Borrower borrower) {
        System.out.print("Enter amount to add to account: Rs. ");
        double amount = getDoubleInput();
        
        if (amount <= 0) {
            System.out.println("Invalid amount. Please enter a positive value.");
            return;
        }
        
        borrower.getUser().addToAccountBalance(amount);
        System.out.println("Rs. " + amount + " added to your account successfully!");
        System.out.println("New account balance: Rs. " + borrower.getUser().getAccountBalance());
    }
    
    private void viewAccountDetails(Borrower borrower) {
        User user = borrower.getUser();
        System.out.println("\n=== ACCOUNT DETAILS ===");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Account Balance: Rs. " + user.getAccountBalance());
        System.out.println("Security Deposit: Rs. " + user.getSecurityDeposit());
        System.out.println("Fine Limit: Rs. " + user.getFineLimit());
        
        double unpaidFines = transactionService.getTotalUnpaidFines(user.getEmail());
        System.out.println("Unpaid Fines: Rs. " + unpaidFines);
        
        int borrowedBooks = transactionService.getCurrentBorrowedBooks(user.getEmail()).size();
        System.out.println("Currently Borrowed Books: " + borrowedBooks + "/3");
    }
    
    // Payment Methods
    private void payFinesMenu(Borrower borrower) {
        double unpaidFines = transactionService.getTotalUnpaidFines(borrower.getUser().getEmail());
        
        if (unpaidFines == 0) {
            System.out.println("You have no unpaid fines.");
            return;
        }
        
        System.out.println("\n=== PAY FINES ===");
        System.out.println("Total unpaid fines: Rs. " + unpaidFines);
        System.out.println("Account balance: Rs. " + borrower.getUser().getAccountBalance());
        System.out.println();
        System.out.println("1. Pay with Cash");
        System.out.println("2. Pay from Account");
        if (borrower.getUser().getAccountBalance() < unpaidFines) {
            System.out.println("3. Add Money to Account");
        }
        System.out.print("Select payment method: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                if (transactionService.payAllUnpaidFinesWithCash(borrower.getUser().getEmail())) {
                    System.out.println("All fines paid successfully with cash!");
                } else {
                    System.out.println("No unpaid fines found.");
                }
                break;
            case 2:
                if (borrower.getUser().getAccountBalance() < unpaidFines) {
                    System.out.println("Insufficient account balance. Please add money first.");
                } else {
                    if (transactionService.payAllUnpaidFinesWithAccount(borrower.getUser().getEmail(), authService)) {
                        System.out.println("All fines paid successfully from account!");
                        System.out.println("Remaining balance: Rs. " + borrower.getUser().getAccountBalance());
                    } else {
                        System.out.println("Payment failed. Please try again.");
                    }
                }
                break;
            case 3:
                if (borrower.getUser().getAccountBalance() >= unpaidFines) {
                    System.out.println("You already have sufficient balance.");
                } else {
                    addMoneyToAccount(borrower);
                }
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void markFineAsPaidCash() {
        System.out.print("Enter borrower email: ");
        String email = scanner.nextLine().trim();
        
        List<FineRecord> unpaidFines = new ArrayList<>();
        for (FineRecord fine : transactionService.getAllFineRecords()) {
            if (fine.getBorrowerEmail().equals(email) && !fine.isPaid()) {
                unpaidFines.add(fine);
            }
        }
        
        if (unpaidFines.isEmpty()) {
            System.out.println("No unpaid fines for this borrower.");
            return;
        }
        
        System.out.println("\n--- Unpaid Fines ---");
        for (int i = 0; i < unpaidFines.size(); i++) {
            FineRecord fine = unpaidFines.get(i);
            String bookTitle = fine.getIsbn().equals("CARD") ? "Membership Card" : 
                bookService.getBookByIsbn(fine.getIsbn()).getTitle();
            System.out.println((i + 1) + ". " + bookTitle + " - Rs. " + fine.getAmount() + 
                " (" + fine.getReason() + ")");
        }
        
        System.out.print("Enter fine number to mark as paid (0 for all): ");
        int fineNum = getIntInput();
        
        if (fineNum == 0) {
            for (FineRecord fine : unpaidFines) {
                fine.setPaid(true);
            }
            System.out.println("All fines marked as paid with cash.");
        } else if (fineNum >= 1 && fineNum <= unpaidFines.size()) {
            unpaidFines.get(fineNum - 1).setPaid(true);
            System.out.println("Fine marked as paid with cash.");
        } else {
            System.out.println("Invalid fine number.");
        }
    }
    
    private void processAccountPayment() {
        System.out.print("Enter borrower email: ");
        String email = scanner.nextLine().trim();
        
        User user = authService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        
        double unpaidFines = transactionService.getTotalUnpaidFines(email);
        if (unpaidFines == 0) {
            System.out.println("This user has no unpaid fines.");
            return;
        }
        
        System.out.println("User: " + user.getName());
        System.out.println("Total unpaid fines: Rs. " + unpaidFines);
        System.out.println("Account balance: Rs. " + user.getAccountBalance());
        
        if (user.getAccountBalance() < unpaidFines) {
            System.out.println("Insufficient account balance for payment.");
            return;
        }
        
        System.out.print("Process payment from account? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y")) {
            if (transactionService.payAllUnpaidFinesWithAccount(email, authService)) {
                System.out.println("Payment processed successfully!");
                System.out.println("Remaining balance: Rs. " + user.getAccountBalance());
            } else {
                System.out.println("Payment failed.");
            }
        } else {
            System.out.println("Payment cancelled.");
        }
    }
    
    private void promoteUserToAdmin() {
        System.out.println("\n--- Promote User to Admin ---");
        System.out.print("Enter email of user to promote to admin: ");
        String email = scanner.nextLine().trim();
        
        User user = authService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        
        if (user.getRole() == UserRole.ADMIN) {
            System.out.println("User is already an admin!");
            return;
        }
        
        // Check if user has any outstanding books or fines
        List<BorrowingRecord> borrowedBooks = transactionService.getCurrentBorrowedBooks(email);
        double unpaidFines = transactionService.getTotalUnpaidFines(email);
        
        if (!borrowedBooks.isEmpty()) {
            System.out.println("Cannot promote user to admin. User has " + borrowedBooks.size() + " borrowed books.");
            System.out.println("Please ensure all books are returned before promotion.");
            return;
        }
        
        if (unpaidFines > 0) {
            System.out.println("Cannot promote user to admin. User has unpaid fines of Rs. " + unpaidFines);
            System.out.println("Please ensure all fines are paid before promotion.");
            return;
        }
        
        System.out.println("User Details: " + user);
        System.out.print("Are you sure you want to promote this user to admin? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            if (authService.promoteToAdmin(email)) {
                System.out.println("User promoted to admin successfully!");
            } else {
                System.out.println("Failed to promote user.");
            }
        } else {
            System.out.println("Promotion cancelled.");
        }
    }
    
    private void deleteUser() {
        System.out.println("\n--- Delete User ---");
        System.out.print("Enter email of user to delete: ");
        String email = scanner.nextLine().trim();
        
        User user = authService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        
        // Prevent deleting the current admin
        System.out.println("Current user details: " + user);
        
        // Check if user has any outstanding books
        List<BorrowingRecord> borrowedBooks = transactionService.getCurrentBorrowedBooks(email);
        if (!borrowedBooks.isEmpty()) {
            System.out.println("Cannot delete user. User has " + borrowedBooks.size() + " borrowed books.");
            System.out.println("Books must be returned before deletion:");
            for (BorrowingRecord record : borrowedBooks) {
                Book book = bookService.getBookByIsbn(record.getIsbn());
                if (book != null) {
                    System.out.println("- " + book.getTitle() + " (Due: " + record.getDueDate().format(DATE_FORMAT) + ")");
                }
            }
            return;
        }
        
        // Check if user has unpaid fines
        double unpaidFines = transactionService.getTotalUnpaidFines(email);
        if (unpaidFines > 0) {
            System.out.println("Warning: User has unpaid fines of Rs. " + unpaidFines);
            System.out.print("Do you still want to delete the user? (y/n): ");
            String confirmFines = scanner.nextLine().trim().toLowerCase();
            if (!confirmFines.equals("y") && !confirmFines.equals("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }
        }
        
        System.out.println("User to delete: " + user);
        System.out.print("Are you sure you want to delete this user? This action cannot be undone. (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            if (authService.deleteUser(email)) {
                System.out.println("User deleted successfully!");
                if (unpaidFines > 0) {
                    System.out.println("Note: Unpaid fines of Rs. " + unpaidFines + " were written off.");
                }
            } else {
                System.out.println("Failed to delete user.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    private void addMoneyToUserAccount() {
        System.out.println("\n--- Add Money to User Account ---");
        System.out.print("Enter email of user: ");
        String email = scanner.nextLine().trim();
        
        User user = authService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        
        System.out.println("User: " + user.getName() + " (" + user.getEmail() + ")");
        System.out.println("Current account balance: Rs. " + user.getAccountBalance());
        System.out.println("Current security deposit: Rs. " + user.getSecurityDeposit());
        
        System.out.print("Enter amount to add to account: Rs. ");
        double amount = getDoubleInput();
        
        if (amount <= 0) {
            System.out.println("Invalid amount. Please enter a positive value.");
            return;
        }
        
        user.addToAccountBalance(amount);
        System.out.println("Rs. " + amount + " added to " + user.getName() + "'s account successfully!");
        System.out.println("New account balance: Rs. " + user.getAccountBalance());
    }
}
