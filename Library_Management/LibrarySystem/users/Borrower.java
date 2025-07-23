package LibrarySystem.users;

import LibrarySystem.models.*;

public class Borrower {
    private User user;
    
    public Borrower(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
    
    public boolean canBorrowBooks() {
        return user.getRole() == UserRole.BORROWER && user.getSecurityDeposit() >= 500;
    }
    
    public boolean hasEnoughDeposit(double requiredAmount) {
        return user.getSecurityDeposit() >= requiredAmount;
    }
    
    public void deductFromDeposit(double amount) {
        if (user.getSecurityDeposit() >= amount) {
            user.setSecurityDeposit(user.getSecurityDeposit() - amount);
        }
    }
    
    public void addToDeposit(double amount) {
        user.setSecurityDeposit(user.getSecurityDeposit() + amount);
    }
}
