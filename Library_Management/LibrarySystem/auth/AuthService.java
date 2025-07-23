package LibrarySystem.auth;

import LibrarySystem.models.*;
import java.util.*;
import java.io.*;

public class AuthService {
    private static final String USERS_FILE = "users.txt";
    private Map<String, User> users;
    
    public AuthService() {
        this.users = new HashMap<>();
        loadUsers();
        
        // Add default admin if no users exist
        if (users.isEmpty()) {
            users.put("admin@library.com", 
                new User("admin@library.com", "Admin", "admin123", UserRole.ADMIN, 0));
            System.out.println("Default admin created: admin@library.com / admin123");
        }
    }
    
    public User authenticate(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public boolean registerUser(String email, String name, String password, UserRole role, double securityDeposit) {
        if (users.containsKey(email)) {
            return false; // User already exists
        }
        
        User user = new User(email, name, password, role, securityDeposit);
        users.put(email, user);
        return true;
    }
    
    public User getUserByEmail(String email) {
        return users.get(email);
    }
    
    public Map<String, User> getAllUsers() {
        return users;
    }
    
    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.println(user.getEmail() + "|" + user.getName() + "|" + 
                    user.getPassword() + "|" + user.getRole() + "|" + user.getSecurityDeposit() + 
                    "|" + user.getAccountBalance());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    User user = new User(parts[0], parts[1], parts[2], 
                        UserRole.valueOf(parts[3]), Double.parseDouble(parts[4]));
                    // Load account balance if present (for backward compatibility)
                    if (parts.length >= 6) {
                        user.setAccountBalance(Double.parseDouble(parts[5]));
                    }
                    users.put(parts[0], user);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, which is fine for first run
        }
    }
    
    public boolean deleteUser(String email) {
        if (users.containsKey(email)) {
            users.remove(email);
            return true;
        }
        return false;
    }
    
    public boolean promoteToAdmin(String email) {
        User user = users.get(email);
        if (user != null && user.getRole() == UserRole.BORROWER) {
            user = new User(user.getEmail(), user.getName(), user.getPassword(), 
                           UserRole.ADMIN, user.getSecurityDeposit());
            user.setAccountBalance(users.get(email).getAccountBalance());
            users.put(email, user);
            return true;
        }
        return false;
    }
}
