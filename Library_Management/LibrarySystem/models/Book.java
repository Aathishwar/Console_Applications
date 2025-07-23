package LibrarySystem.models;

public class Book {
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
