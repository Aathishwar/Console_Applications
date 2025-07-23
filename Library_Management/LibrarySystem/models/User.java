package LibrarySystem.models;

public class User {
    private String email;
    private String name;
    private String password;
    private UserRole role;
    private double securityDeposit;
    private double accountBalance = 0.0; // Account balance for payments
    private double fineLimit = 1000.0; // Default fine limit
    
    public User(String email, String name, String password, UserRole role, double securityDeposit) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.securityDeposit = securityDeposit;
        this.accountBalance = 0.0;
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
    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }
    public void addToAccountBalance(double amount) { this.accountBalance += amount; }
    public boolean deductFromAccountBalance(double amount) { 
        if (accountBalance >= amount) {
            accountBalance -= amount;
            return true;
        }
        return false;
    }
    public double getFineLimit() { return fineLimit; }
    public void setFineLimit(double fineLimit) { this.fineLimit = fineLimit; }
    
    @Override
    public String toString() {
        return "User{email='" + email + "', name='" + name + "', role=" + role + 
               ", securityDeposit=" + securityDeposit + ", accountBalance=" + accountBalance + "}";
    }
}
