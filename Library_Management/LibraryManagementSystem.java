import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LibraryManagementSystem {
    private static final String USERS_FILE = "users.txt";
    private static final String BOOKS_FILE = "books.txt";
    private static final String BORROWING_FILE = "borrowing.txt";
    private static final String FINES_FILE = "fines.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private Map<String, User> users;
    private Map<String, Book> books;
    private List<BorrowingRecord> borrowingRecords;
    private List<FineRecord> fineRecords;
    private Scanner scanner;
    
    public LibraryManagementSystem() {
        this.users = new HashMap<>();
        this.books = new HashMap<>();
        this.borrowingRecords = new ArrayList<>();
        this.fineRecords = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadData();
    }
    
    public static void main(String[] args) {
        LibraryManagementSystem system = new LibraryManagementSystem();
        system.start();
    }
    
    public void start() {
        System.out.println("==============================================");
        System.out.println("   Welcome to Library Management System");
        System.out.println("==============================================");
        
        User currentUser = authenticate();
        if (currentUser != null) {
            if (currentUser.getRole() == UserRole.ADMIN) {
                adminMenu(currentUser);
            } else {
                borrowerMenu(currentUser);
            }
        }
        
        saveData();
        scanner.close();
    }
    
    // Authentication Module
    private User authenticate() {
        System.out.println("\n--- Authentication ---");
        System.out.print("Enter Email ID: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("\nAuthentication successful!");
            System.out.println("Welcome, " + user.getName() + "!");
            return user;
        } else {
            System.out.println("Invalid credentials. Access denied.");
            return null;
        }
    }
    
    // Admin Menu
    private void adminMenu(User admin) {
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
                    bookInventoryMenu(admin);
                    break;
                case 2:
                    userManagementMenu(admin);
                    break;
                case 3:
                    reportsMenu(admin);
                    break;
                case 4:
                    fineManagementMenu(admin);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    // Borrower Menu
    private void borrowerMenu(User borrower) {
        while (true) {
            System.out.println("\n=== BORROWER MENU ===");
            System.out.println("Current Security Deposit: Rs. " + borrower.getSecurityDeposit());
            System.out.println("Books Currently Borrowed: " + getCurrentBorrowedBooks(borrower.getEmail()).size() + "/3");
            System.out.println();
            System.out.println("1. View Available Books");
            System.out.println("2. Search Books");
            System.out.println("3. Borrow Books");
            System.out.println("4. Return Books");
            System.out.println("5. Extend Book Tenure");
            System.out.println("6. Report Lost Book/Card");
            System.out.println("7. View My Reports");
            System.out.println("8. Logout");
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
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    // Book Inventory Management
    private void bookInventoryMenu(User admin) {
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
        
        if (books.containsKey(isbn)) {
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
        
        Book book = new Book(isbn, title, author, quantity, cost);
        books.put(isbn, book);
        
        System.out.println("Book added successfully!");
    }
    
    private void modifyBook() {
        System.out.println("\n--- Modify Book Details ---");
        System.out.print("Enter ISBN of book to modify: ");
        String isbn = scanner.nextLine().trim();
        
        Book book = books.get(isbn);
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
        
        Book book = books.get(isbn);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        // Check if book is currently borrowed
        boolean isBorrowed = borrowingRecords.stream()
            .anyMatch(record -> record.getIsbn().equals(isbn) && record.getReturnDate() == null);
        
        if (isBorrowed) {
            System.out.println("Cannot delete book. It is currently borrowed by someone.");
            return;
        }
        
        System.out.println("Book to delete: " + book);
        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            books.remove(isbn);
            System.out.println("Book deleted successfully!");
        }
    }
    
    private void viewBooksSortedByName() {
        System.out.println("\n--- Books Sorted by Name ---");
        books.values().stream()
            .sorted((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()))
            .forEach(System.out::println);
    }
    
    private void viewBooksSortedByQuantity() {
        System.out.println("\n--- Books Sorted by Available Quantity ---");
        books.values().stream()
            .sorted((b1, b2) -> Integer.compare(b2.getAvailableQuantity(), b1.getAvailableQuantity()))
            .forEach(System.out::println);
    }
    
    private void searchBooksMenu() {
        System.out.println("\n--- Search Books ---");
        System.out.println("1. Search by Title");
        System.out.println("2. Search by ISBN");
        System.out.println("3. Search by Author");
        System.out.print("Select option: ");
        
        int choice = getIntInput();
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine().trim().toLowerCase();
        
        List<Book> results = new ArrayList<>();
        
        switch (choice) {
            case 1:
                List<Book> results1 = new ArrayList<>();
                for (Book book : books.values()) {
                    if (book.getTitle().toLowerCase().contains(searchTerm)) {
                        results1.add(book);
                    }
                }
                results = results1;
                break;
            case 2:
                Book book = books.get(searchTerm.toUpperCase());
                if (book != null) results.add(book);
                break;
            case 3:
                List<Book> results3 = new ArrayList<>();
                for (Book b : books.values()) {
                    if (b.getAuthor().toLowerCase().contains(searchTerm)) {
                        results3.add(b);
                    }
                }
                results = results3;
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        
        if (results.isEmpty()) {
            System.out.println("No books found matching your search.");
        } else {
            System.out.println("\n--- Search Results ---");
            results.forEach(System.out::println);
        }
    }
    
    // User Management
    private void userManagementMenu(User admin) {
        while (true) {
            System.out.println("\n=== USER MANAGEMENT ===");
            System.out.println("1. Add Admin");
            System.out.println("2. Add Borrower");
            System.out.println("3. View All Users");
            System.out.println("4. Modify User");
            System.out.println("5. Manage Fine Limits");
            System.out.println("6. Back to Main Menu");
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
                    manageFineLimit();
                    break;
                case 6:
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
        
        if (users.containsKey(email)) {
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
        
        User user = new User(email, name, password, role, securityDeposit);
        users.put(email, user);
        
        System.out.println(role + " added successfully!");
    }
    
    private void viewAllUsers() {
        System.out.println("\n--- All Users ---");
        users.values().forEach(System.out::println);
    }
    
    private void modifyUser() {
        System.out.println("\n--- Modify User ---");
        System.out.print("Enter Email ID of user to modify: ");
        String email = scanner.nextLine().trim();
        
        User user = users.get(email);
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
    
    private void manageFineLimit() {
        System.out.println("\n--- Manage Fine Limits ---");
        System.out.print("Enter borrower email: ");
        String email = scanner.nextLine().trim();
        
        User user = users.get(email);
        if (user == null || user.getRole() != UserRole.BORROWER) {
            System.out.println("Borrower not found!");
            return;
        }
        
        System.out.println("Current Fine Limit: Rs. " + user.getFineLimit());
        System.out.print("Enter new fine limit: ");
        double newLimit = getDoubleInput();
        
        user.setFineLimit(newLimit);
        System.out.println("Fine limit updated successfully!");
    }
    
    // Borrowing Management
    private void borrowBooksMenu(User borrower) {
        if (borrower.getSecurityDeposit() < 500) {
            System.out.println("Insufficient security deposit. Minimum Rs. 500 required to borrow books.");
            return;
        }
        
        List<BorrowingRecord> currentBorrowedBooks = getCurrentBorrowedBooks(borrower.getEmail());
        if (currentBorrowedBooks.size() >= 3) {
            System.out.println("You have already borrowed the maximum number of books (3).");
            return;
        }
        
        List<String> cart = new ArrayList<>();
        
        while (true) {
            System.out.println("\n=== BOOK BORROWING ===");
            System.out.println("Books in cart: " + cart.size());
            System.out.println("Currently borrowed: " + currentBorrowedBooks.size());
            System.out.println("Available slots: " + (3 - currentBorrowedBooks.size() - cart.size()));
            
            System.out.println("\n1. Add book to cart");
            System.out.println("2. Remove book from cart");
            System.out.println("3. View cart");
            System.out.println("4. Checkout");
            System.out.println("5. Cancel");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    addBookToCart(cart, currentBorrowedBooks, borrower);
                    break;
                case 2:
                    removeBookFromCart(cart);
                    break;
                case 3:
                    viewCart(cart);
                    break;
                case 4:
                    if (cart.isEmpty()) {
                        System.out.println("Cart is empty!");
                    } else {
                        checkoutBooks(cart, borrower);
                        return;
                    }
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void addBookToCart(List<String> cart, List<BorrowingRecord> currentBorrowedBooks, User borrower) {
        if (cart.size() + currentBorrowedBooks.size() >= 3) {
            System.out.println("Cannot add more books. Maximum 3 books can be borrowed at a time.");
            return;
        }
        
        System.out.print("Enter ISBN or Book Title: ");
        String searchTerm = scanner.nextLine().trim();
        
        Book book = findBook(searchTerm);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        if (book.getAvailableQuantity() <= 0) {
            System.out.println("Book is not available for borrowing.");
            return;
        }
        
        // Check if already borrowed
        boolean alreadyBorrowed = currentBorrowedBooks.stream()
            .anyMatch(record -> record.getIsbn().equals(book.getIsbn()));
        
        if (alreadyBorrowed) {
            System.out.println("You have already borrowed this book!");
            return;
        }
        
        // Check if already in cart
        if (cart.contains(book.getIsbn())) {
            System.out.println("Book is already in your cart!");
            return;
        }
        
        cart.add(book.getIsbn());
        System.out.println("Book added to cart: " + book.getTitle());
    }
    
    private void removeBookFromCart(List<String> cart) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }
        
        System.out.println("Books in cart:");
        for (int i = 0; i < cart.size(); i++) {
            Book book = books.get(cart.get(i));
            System.out.println((i + 1) + ". " + book.getTitle());
        }
        
        System.out.print("Enter book number to remove: ");
        int bookNum = getIntInput();
        
        if (bookNum >= 1 && bookNum <= cart.size()) {
            String removedIsbn = cart.remove(bookNum - 1);
            Book removedBook = books.get(removedIsbn);
            System.out.println("Removed from cart: " + removedBook.getTitle());
        } else {
            System.out.println("Invalid book number.");
        }
    }
    
    private void viewCart(List<String> cart) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }
        
        System.out.println("\n--- Your Cart ---");
        for (String isbn : cart) {
            Book book = books.get(isbn);
            System.out.println(book);
        }
    }
    
    private void checkoutBooks(List<String> cart, User borrower) {
        System.out.println("\n--- Checkout Summary ---");
        viewCart(cart);
        
        System.out.print("Confirm checkout? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Checkout cancelled.");
            return;
        }
        
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(15);
        
        for (String isbn : cart) {
            Book book = books.get(isbn);
            book.setAvailableQuantity(book.getAvailableQuantity() - 1);
            
            BorrowingRecord record = new BorrowingRecord(
                borrower.getEmail(), isbn, borrowDate, dueDate
            );
            borrowingRecords.add(record);
        }
        
        System.out.println("Books borrowed successfully!");
        System.out.println("Due date: " + dueDate.format(DATE_FORMAT));
        System.out.println("Please return books on time to avoid fines.");
        
        cart.clear();
    }
    
    private void returnBooksMenu(User borrower) {
        List<BorrowingRecord> borrowedBooks = getCurrentBorrowedBooks(borrower.getEmail());
        
        if (borrowedBooks.isEmpty()) {
            System.out.println("You have no books to return.");
            return;
        }
        
        System.out.println("\n--- Your Borrowed Books ---");
        for (int i = 0; i < borrowedBooks.size(); i++) {
            BorrowingRecord record = borrowedBooks.get(i);
            Book book = books.get(record.getIsbn());
            long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
            
            System.out.println((i + 1) + ". " + book.getTitle() + 
                " (Due: " + record.getDueDate().format(DATE_FORMAT) + 
                (daysOverdue > 0 ? ", OVERDUE by " + daysOverdue + " days" : "") + ")");
        }
        
        System.out.print("Enter book number to return: ");
        int bookNum = getIntInput();
        
        if (bookNum >= 1 && bookNum <= borrowedBooks.size()) {
            BorrowingRecord record = borrowedBooks.get(bookNum - 1);
            returnBook(record, borrower);
        } else {
            System.out.println("Invalid book number.");
        }
    }
    
    private void returnBook(BorrowingRecord record, User borrower) {
        System.out.print("Enter return date (DD/MM/YYYY): ");
        String dateStr = scanner.nextLine().trim();
        
        LocalDate returnDate;
        try {
            returnDate = LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            System.out.println("Invalid date format!");
            return;
        }
        
        record.setReturnDate(returnDate);
        
        // Update book quantity
        Book book = books.get(record.getIsbn());
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        
        // Calculate fine if overdue
        long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
        if (daysOverdue > 0) {
            double fine = calculateOverdueFine(daysOverdue, book.getCost());
            
            FineRecord fineRecord = new FineRecord(
                borrower.getEmail(), record.getIsbn(), fine, 
                FineReason.OVERDUE, LocalDate.now()
            );
            fineRecords.add(fineRecord);
            
            System.out.println("Book returned with fine: Rs. " + fine);
            System.out.print("Pay fine by cash (c) or deduct from deposit (d)? ");
            String paymentMethod = scanner.nextLine().trim().toLowerCase();
            
            if (paymentMethod.equals("d") || paymentMethod.equals("deposit")) {
                if (borrower.getSecurityDeposit() >= fine) {
                    borrower.setSecurityDeposit(borrower.getSecurityDeposit() - fine);
                    fineRecord.setPaid(true);
                    System.out.println("Fine deducted from security deposit.");
                    System.out.println("Remaining deposit: Rs. " + borrower.getSecurityDeposit());
                } else {
                    System.out.println("Insufficient deposit. Fine must be paid in cash.");
                }
            }
        } else {
            System.out.println("Book returned successfully without any fine.");
        }
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
    
    private void extendTenureMenu(User borrower) {
        List<BorrowingRecord> borrowedBooks = getCurrentBorrowedBooks(borrower.getEmail());
        
        if (borrowedBooks.isEmpty()) {
            System.out.println("You have no books to extend.");
            return;
        }
        
        System.out.println("\n--- Your Borrowed Books ---");
        for (int i = 0; i < borrowedBooks.size(); i++) {
            BorrowingRecord record = borrowedBooks.get(i);
            Book book = books.get(record.getIsbn());
            System.out.println((i + 1) + ". " + book.getTitle() + 
                " (Due: " + record.getDueDate().format(DATE_FORMAT) + 
                ", Extensions: " + record.getExtensions() + "/2)");
        }
        
        System.out.print("Enter book number to extend: ");
        int bookNum = getIntInput();
        
        if (bookNum >= 1 && bookNum <= borrowedBooks.size()) {
            BorrowingRecord record = borrowedBooks.get(bookNum - 1);
            
            if (record.getExtensions() >= 2) {
                System.out.println("Maximum extensions (2) already reached for this book.");
                return;
            }
            
            record.setDueDate(record.getDueDate().plusDays(15));
            record.setExtensions(record.getExtensions() + 1);
            
            System.out.println("Book tenure extended successfully!");
            System.out.println("New due date: " + record.getDueDate().format(DATE_FORMAT));
        } else {
            System.out.println("Invalid book number.");
        }
    }
    
    private void reportLostMenu(User borrower) {
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
                reportLostCard(borrower);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void reportLostBook(User borrower) {
        List<BorrowingRecord> borrowedBooks = getCurrentBorrowedBooks(borrower.getEmail());
        
        if (borrowedBooks.isEmpty()) {
            System.out.println("You have no borrowed books to report as lost.");
            return;
        }
        
        System.out.println("\n--- Your Borrowed Books ---");
        for (int i = 0; i < borrowedBooks.size(); i++) {
            BorrowingRecord record = borrowedBooks.get(i);
            Book book = books.get(record.getIsbn());
            System.out.println((i + 1) + ". " + book.getTitle());
        }
        
        System.out.print("Enter book number to report as lost: ");
        int bookNum = getIntInput();
        
        if (bookNum >= 1 && bookNum <= borrowedBooks.size()) {
            BorrowingRecord record = borrowedBooks.get(bookNum - 1);
            Book book = books.get(record.getIsbn());
            
            double fine = book.getCost() * 0.5; // 50% of book cost
            
            FineRecord fineRecord = new FineRecord(
                borrower.getEmail(), record.getIsbn(), fine,
                FineReason.LOST_BOOK, LocalDate.now()
            );
            fineRecords.add(fineRecord);
            
            // Mark book as returned (lost)
            record.setReturnDate(LocalDate.now());
            
            System.out.println("Book reported as lost.");
            System.out.println("Fine amount: Rs. " + fine);
            System.out.print("Pay fine by cash (c) or deduct from deposit (d)? ");
            String paymentMethod = scanner.nextLine().trim().toLowerCase();
            
            if (paymentMethod.equals("d") || paymentMethod.equals("deposit")) {
                if (borrower.getSecurityDeposit() >= fine) {
                    borrower.setSecurityDeposit(borrower.getSecurityDeposit() - fine);
                    fineRecord.setPaid(true);
                    System.out.println("Fine deducted from security deposit.");
                    System.out.println("Remaining deposit: Rs. " + borrower.getSecurityDeposit());
                } else {
                    System.out.println("Insufficient deposit. Fine must be paid in cash.");
                }
            }
        } else {
            System.out.println("Invalid book number.");
        }
    }
    
    private void reportLostCard(User borrower) {
        double fine = 10.0; // Rs. 10 for lost card
        
        FineRecord fineRecord = new FineRecord(
            borrower.getEmail(), "CARD", fine,
            FineReason.LOST_CARD, LocalDate.now()
        );
        fineRecords.add(fineRecord);
        
        System.out.println("Membership card reported as lost.");
        System.out.println("Fine amount: Rs. " + fine);
        System.out.print("Pay fine by cash (c) or deduct from deposit (d)? ");
        String paymentMethod = scanner.nextLine().trim().toLowerCase();
        
        if (paymentMethod.equals("d") || paymentMethod.equals("deposit")) {
            if (borrower.getSecurityDeposit() >= fine) {
                borrower.setSecurityDeposit(borrower.getSecurityDeposit() - fine);
                fineRecord.setPaid(true);
                System.out.println("Fine deducted from security deposit.");
                System.out.println("Remaining deposit: Rs. " + borrower.getSecurityDeposit());
            } else {
                System.out.println("Insufficient deposit. Fine must be paid in cash.");
            }
        }
    }
    
    // Reports
    private void reportsMenu(User admin) {
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
                    lowQuantityBooksReport();
                    break;
                case 2:
                    neverBorrowedBooksReport();
                    break;
                case 3:
                    mostBorrowedBooksReport();
                    break;
                case 4:
                    outstandingBooksReport();
                    break;
                case 5:
                    bookStatusReport();
                    break;
                case 6:
                    allFinesReport();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void lowQuantityBooksReport() {
        System.out.print("Enter minimum quantity threshold: ");
        int threshold = getIntInput();
        
        System.out.println("\n--- Books with Low Quantity (≤ " + threshold + ") ---");
        books.values().stream()
            .filter(book -> book.getAvailableQuantity() <= threshold)
            .sorted((b1, b2) -> Integer.compare(b1.getAvailableQuantity(), b2.getAvailableQuantity()))
            .forEach(System.out::println);
    }
    
    private void neverBorrowedBooksReport() {
        System.out.println("\n--- Books Never Borrowed ---");
        Set<String> borrowedIsbns = new HashSet<>();
        for (BorrowingRecord record : borrowingRecords) {
            borrowedIsbns.add(record.getIsbn());
        }
        
        books.values().stream()
            .filter(book -> !borrowedIsbns.contains(book.getIsbn()))
            .sorted((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()))
            .forEach(System.out::println);
    }
    
    private void mostBorrowedBooksReport() {
        System.out.println("\n--- Most Borrowed Books ---");
        Map<String, Long> borrowCounts = new HashMap<>();
        
        for (BorrowingRecord record : borrowingRecords) {
            borrowCounts.merge(record.getIsbn(), 1L, Long::sum);
        }
        
        borrowCounts.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
            .limit(10)
            .forEach(entry -> {
                Book book = books.get(entry.getKey());
                System.out.println(book.getTitle() + " - Borrowed " + entry.getValue() + " times");
            });
    }
    
    private void outstandingBooksReport() {
        System.out.print("Enter date to check outstanding books (DD/MM/YYYY): ");
        String dateStr = scanner.nextLine().trim();
        
        LocalDate checkDate;
        try {
            checkDate = LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            System.out.println("Invalid date format!");
            return;
        }
        
        System.out.println("\n--- Outstanding Books as of " + dateStr + " ---");
        borrowingRecords.stream()
            .filter(record -> record.getReturnDate() == null && record.getDueDate().isBefore(checkDate))
            .forEach(record -> {
                Book book = books.get(record.getIsbn());
                User borrower = users.get(record.getBorrowerEmail());
                long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), checkDate);
                System.out.println(borrower.getName() + " (" + borrower.getEmail() + ") - " +
                    book.getTitle() + " - Overdue by " + daysOverdue + " days");
            });
    }
    
    private void bookStatusReport() {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim().toUpperCase();
        
        Book book = books.get(isbn);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        System.out.println("\n--- Book Status Report ---");
        System.out.println("Book: " + book);
        
        BorrowingRecord currentRecord = borrowingRecords.stream()
            .filter(record -> record.getIsbn().equals(isbn) && record.getReturnDate() == null)
            .findFirst()
            .orElse(null);
        
        if (currentRecord == null) {
            System.out.println("Status: Available in library");
        } else {
            User borrower = users.get(currentRecord.getBorrowerEmail());
            System.out.println("Status: Currently borrowed");
            System.out.println("Borrowed by: " + borrower.getName() + " (" + borrower.getEmail() + ")");
            System.out.println("Borrowed date: " + currentRecord.getBorrowDate().format(DATE_FORMAT));
            System.out.println("Due date: " + currentRecord.getDueDate().format(DATE_FORMAT));
            System.out.println("Expected return: " + currentRecord.getDueDate().format(DATE_FORMAT));
        }
    }
    
    private void allFinesReport() {
        System.out.println("\n--- All Fines Report ---");
        if (fineRecords.isEmpty()) {
            System.out.println("No fines recorded.");
            return;
        }
        
        fineRecords.stream()
            .sorted((f1, f2) -> f2.getFineDate().compareTo(f1.getFineDate()))
            .forEach(fine -> {
                User borrower = users.get(fine.getBorrowerEmail());
                String bookTitle = fine.getIsbn().equals("CARD") ? "Membership Card" : 
                    books.get(fine.getIsbn()).getTitle();
                System.out.println(borrower.getName() + " - " + bookTitle + " - Rs. " + 
                    fine.getAmount() + " (" + fine.getReason() + ") - " + 
                    (fine.isPaid() ? "PAID" : "UNPAID"));
            });
    }
    
    private void borrowerReportsMenu(User borrower) {
        while (true) {
            System.out.println("\n=== MY REPORTS ===");
            System.out.println("1. My Fine History");
            System.out.println("2. My Borrowing History");
            System.out.println("3. Back to Main Menu");
            System.out.print("Select option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    myFineHistory(borrower);
                    break;
                case 2:
                    myBorrowingHistory(borrower);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void myFineHistory(User borrower) {
        System.out.println("\n--- My Fine History ---");
        List<FineRecord> myFines = new ArrayList<>();
        for (FineRecord fine : fineRecords) {
            if (fine.getBorrowerEmail().equals(borrower.getEmail())) {
                myFines.add(fine);
            }
        }
        
        // Sort by date (newest first)
        myFines.sort((f1, f2) -> f2.getFineDate().compareTo(f1.getFineDate()));
        
        if (myFines.isEmpty()) {
            System.out.println("No fines recorded.");
            return;
        }
        
        double totalUnpaid = 0;
        for (FineRecord fine : myFines) {
            String bookTitle = fine.getIsbn().equals("CARD") ? "Membership Card" : 
                books.get(fine.getIsbn()).getTitle();
            System.out.println(fine.getFineDate().format(DATE_FORMAT) + " - " + bookTitle + 
                " - Rs. " + fine.getAmount() + " (" + fine.getReason() + ") - " + 
                (fine.isPaid() ? "PAID" : "UNPAID"));
            
            if (!fine.isPaid()) {
                totalUnpaid += fine.getAmount();
            }
        }
        
        System.out.println("\nTotal unpaid fines: Rs. " + totalUnpaid);
    }
    
    private void myBorrowingHistory(User borrower) {
        System.out.println("\n--- My Borrowing History ---");
        List<BorrowingRecord> myRecords = new ArrayList<>();
        for (BorrowingRecord record : borrowingRecords) {
            if (record.getBorrowerEmail().equals(borrower.getEmail())) {
                myRecords.add(record);
            }
        }
        
        // Sort by borrow date (newest first)
        myRecords.sort((r1, r2) -> r2.getBorrowDate().compareTo(r1.getBorrowDate()));
        
        if (myRecords.isEmpty()) {
            System.out.println("No borrowing history.");
            return;
        }
        
        for (BorrowingRecord record : myRecords) {
            Book book = books.get(record.getIsbn());
            String status = record.getReturnDate() == null ? "CURRENTLY BORROWED" : "RETURNED";
            String returnInfo = record.getReturnDate() == null ? 
                "Due: " + record.getDueDate().format(DATE_FORMAT) :
                "Returned: " + record.getReturnDate().format(DATE_FORMAT);
            
            System.out.println(book.getTitle() + " - Borrowed: " + 
                record.getBorrowDate().format(DATE_FORMAT) + " - " + returnInfo + " - " + status);
        }
    }
    
    private void fineManagementMenu(User admin) {
        System.out.println("\n=== FINE MANAGEMENT ===");
        System.out.println("1. Mark Fine as Paid");
        System.out.println("2. View Unpaid Fines");
        System.out.print("Select option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                markFineAsPaid();
                break;
            case 2:
                viewUnpaidFines();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void markFineAsPaid() {
        System.out.print("Enter borrower email: ");
        String email = scanner.nextLine().trim();
        
        List<FineRecord> unpaidFines = new ArrayList<>();
        for (FineRecord fine : fineRecords) {
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
                books.get(fine.getIsbn()).getTitle();
            System.out.println((i + 1) + ". " + bookTitle + " - Rs. " + fine.getAmount() + 
                " (" + fine.getReason() + ")");
        }
        
        System.out.print("Enter fine number to mark as paid: ");
        int fineNum = getIntInput();
        
        if (fineNum >= 1 && fineNum <= unpaidFines.size()) {
            unpaidFines.get(fineNum - 1).setPaid(true);
            System.out.println("Fine marked as paid.");
        } else {
            System.out.println("Invalid fine number.");
        }
    }
    
    private void viewUnpaidFines() {
        System.out.println("\n--- All Unpaid Fines ---");
        List<FineRecord> unpaidFines = new ArrayList<>();
        for (FineRecord fine : fineRecords) {
            if (!fine.isPaid()) {
                unpaidFines.add(fine);
            }
        }
        
        // Sort by date (newest first)
        unpaidFines.sort((f1, f2) -> f2.getFineDate().compareTo(f1.getFineDate()));
        
        if (unpaidFines.isEmpty()) {
            System.out.println("No unpaid fines.");
            return;
        }
        
        for (FineRecord fine : unpaidFines) {
            User borrower = users.get(fine.getBorrowerEmail());
            String bookTitle = fine.getIsbn().equals("CARD") ? "Membership Card" : 
                books.get(fine.getIsbn()).getTitle();
            System.out.println(borrower.getName() + " (" + borrower.getEmail() + ") - " + 
                bookTitle + " - Rs. " + fine.getAmount() + " (" + fine.getReason() + ")");
        }
    }
    
    private void viewAvailableBooks() {
        System.out.println("\n--- Available Books ---");
        books.values().stream()
            .filter(book -> book.getAvailableQuantity() > 0)
            .sorted((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()))
            .forEach(System.out::println);
    }
    
    // Utility Methods
    private List<BorrowingRecord> getCurrentBorrowedBooks(String borrowerEmail) {
        List<BorrowingRecord> result = new ArrayList<>();
        for (BorrowingRecord record : borrowingRecords) {
            if (record.getBorrowerEmail().equals(borrowerEmail) && record.getReturnDate() == null) {
                result.add(record);
            }
        }
        return result;
    }
    
    private Book findBook(String searchTerm) {
        // First try to find by ISBN
        Book book = books.get(searchTerm.toUpperCase());
        if (book != null) {
            return book;
        }
        
        // Then try to find by title
        return books.values().stream()
            .filter(b -> b.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
            .findFirst()
            .orElse(null);
    }
    
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
    
    // Data Persistence Methods
    private void loadData() {
        loadUsers();
        loadBooks();
        loadBorrowingRecords();
        loadFineRecords();
        
        // Add default admin if no users exist
        if (users.isEmpty()) {
            users.put("admin@library.com", new User("admin@library.com", "Admin", "admin123", UserRole.ADMIN, 0));
            System.out.println("Default admin created: admin@library.com / admin123");
        }
        
        // Add sample books if no books exist
        if (books.isEmpty()) {
            addSampleBooks();
        }
    }
    
    private void addSampleBooks() {
        books.put("978-0134685991", new Book("978-0134685991", "Effective Java", "Joshua Bloch", 5, 2500.0));
        books.put("978-0596009205", new Book("978-0596009205", "Head First Design Patterns", "Eric Freeman", 3, 2200.0));
        books.put("978-0132350884", new Book("978-0132350884", "Clean Code", "Robert Martin", 4, 2800.0));
        books.put("978-0321356680", new Book("978-0321356680", "Effective Java Programming", "Joshua Bloch", 2, 2600.0));
        books.put("978-0201633610", new Book("978-0201633610", "Design Patterns", "Gang of Four", 3, 3000.0));
        System.out.println("Sample books added to the library.");
    }
    
    private void saveData() {
        saveUsers();
        saveBooks();
        saveBorrowingRecords();
        saveFineRecords();
    }
    
    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    User user = new User(parts[0], parts[1], parts[2], 
                        UserRole.valueOf(parts[3]), Double.parseDouble(parts[4]));
                    users.put(parts[0], user);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, which is fine for first run
        }
    }
    
    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.println(user.getEmail() + "|" + user.getName() + "|" + 
                    user.getPassword() + "|" + user.getRole() + "|" + user.getSecurityDeposit());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    private void loadBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    Book book = new Book(parts[0], parts[1], parts[2], 
                        Integer.parseInt(parts[3]), Double.parseDouble(parts[4]));
                    books.put(parts[0], book);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, which is fine for first run
        }
    }
    
    private void saveBooks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books.values()) {
                writer.println(book.getIsbn() + "|" + book.getTitle() + "|" + 
                    book.getAuthor() + "|" + book.getAvailableQuantity() + "|" + book.getCost());
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
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
}

// Supporting Classes and Enums
enum UserRole {
    ADMIN, BORROWER
}

enum FineReason {
    OVERDUE, LOST_BOOK, LOST_CARD
}

class User {
    private String email;
    private String name;
    private String password;
    private UserRole role;
    private double securityDeposit;
    private double fineLimit = 1000.0; // Default fine limit
    
    public User(String email, String name, String password, UserRole role, double securityDeposit) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.securityDeposit = securityDeposit;
    }
    
    // Getters and Setters
    public String getEmail() { return email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public double getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(double securityDeposit) { this.securityDeposit = securityDeposit; }
    public double getFineLimit() { return fineLimit; }
    public void setFineLimit(double fineLimit) { this.fineLimit = fineLimit; }
    
    @Override
    public String toString() {
        return "User{email='" + email + "', name='" + name + "', role=" + role + 
               ", securityDeposit=" + securityDeposit + "}";
    }
}

class Book {
    private String isbn;
    private String title;
    private String author;
    private int availableQuantity;
    private double cost;
    
    public Book(String isbn, String title, String author, int availableQuantity, double cost) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.availableQuantity = availableQuantity;
        this.cost = cost;
    }
    
    // Getters and Setters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    
    @Override
    public String toString() {
        return "Book{ISBN='" + isbn + "', title='" + title + "', author='" + author + 
               "', available=" + availableQuantity + ", cost=Rs." + cost + "}";
    }
}

class BorrowingRecord {
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

class FineRecord {
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
