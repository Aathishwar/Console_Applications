package LibrarySystem.books;

import LibrarySystem.models.*;
import java.util.*;
import java.io.*;

public class BookService {
    private static final String BOOKS_FILE = "books.txt";
    private Map<String, Book> books;
    
    public BookService() {
        this.books = new HashMap<>();
        loadBooks();
        
        // Add sample books if no books exist
        if (books.isEmpty()) {
            addSampleBooks();
        }
    }
    
    public boolean addBook(String isbn, String title, String author, int quantity, double cost) {
        if (books.containsKey(isbn)) {
            return false; // Book already exists
        }
        
        Book book = new Book(isbn, title, author, quantity, cost);
        books.put(isbn, book);
        return true;
    }
    
    public Book getBookByIsbn(String isbn) {
        return books.get(isbn);
    }
    
    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }
    
    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getAvailableQuantity() > 0) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }
    
    public List<Book> searchBooksByTitle(String title) {
        List<Book> results = new ArrayList<>();
        String searchTerm = title.toLowerCase();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(book);
            }
        }
        return results;
    }
    
    public List<Book> searchBooksByAuthor(String author) {
        List<Book> results = new ArrayList<>();
        String searchTerm = author.toLowerCase();
        for (Book book : books.values()) {
            if (book.getAuthor().toLowerCase().contains(searchTerm)) {
                results.add(book);
            }
        }
        return results;
    }
    
    public List<Book> getBooksSortedByTitle() {
        List<Book> sortedBooks = new ArrayList<>(books.values());
        sortedBooks.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
        return sortedBooks;
    }
    
    public List<Book> getBooksSortedByQuantity() {
        List<Book> sortedBooks = new ArrayList<>(books.values());
        sortedBooks.sort((b1, b2) -> Integer.compare(b2.getAvailableQuantity(), b1.getAvailableQuantity()));
        return sortedBooks;
    }
    
    public List<Book> getBooksWithLowQuantity(int threshold) {
        List<Book> lowQuantityBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getAvailableQuantity() <= threshold) {
                lowQuantityBooks.add(book);
            }
        }
        lowQuantityBooks.sort((b1, b2) -> Integer.compare(b1.getAvailableQuantity(), b2.getAvailableQuantity()));
        return lowQuantityBooks;
    }
    
    public boolean deleteBook(String isbn) {
        return books.remove(isbn) != null;
    }
    
    public Book findBook(String searchTerm) {
        // First try to find by ISBN
        Book book = books.get(searchTerm.toUpperCase());
        if (book != null) {
            return book;
        }
        
        // Then try to find by title
        for (Book b : books.values()) {
            if (b.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) {
                return b;
            }
        }
        return null;
    }
    
    public void saveBooks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books.values()) {
                writer.println(book.getIsbn() + "|" + book.getTitle() + "|" + 
                    book.getAuthor() + "|" + book.getAvailableQuantity() + "|" + book.getCost());
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
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
    
    private void addSampleBooks() {
        books.put("978-0134685991", new Book("978-0134685991", "Effective Java", "Joshua Bloch", 5, 2500.0));
        books.put("978-0596009205", new Book("978-0596009205", "Head First Design Patterns", "Eric Freeman", 3, 2200.0));
        books.put("978-0132350884", new Book("978-0132350884", "Clean Code", "Robert Martin", 4, 2800.0));
        books.put("978-0321356680", new Book("978-0321356680", "Effective Java Programming", "Joshua Bloch", 2, 2600.0));
        books.put("978-0201633610", new Book("978-0201633610", "Design Patterns", "Gang of Four", 3, 3000.0));
        System.out.println("Sample books added to the library.");
    }
}
