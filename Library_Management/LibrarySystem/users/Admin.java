package LibrarySystem.users;

import LibrarySystem.models.*;

public class Admin {
    private User user;
    
    public Admin(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
    
    // Admin-specific methods can be added here
    public boolean canManageBooks() {
        return user.getRole() == UserRole.ADMIN;
    }
    
    public boolean canManageUsers() {
        return user.getRole() == UserRole.ADMIN;
    }
    
    public boolean canViewAllReports() {
        return user.getRole() == UserRole.ADMIN;
    }
}
