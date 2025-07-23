# Library Management System

A comprehensive console-based Library Management System built in Java that handles both Administrator and Borrower functionalities with complete authentication, book management, borrowing operations, fine calculations, and detailed reporting.

## Features

### Module A: Authentication and Welcome Menu
- Email-based authentication for both Admins and Borrowers
- Role-based menu system
- Secure login with password verification

### Module B: Book Inventory Management (Admin Only)
- Add new books with ISBN, title, author, quantity, and cost
- Modify book details including available quantity
- Delete books (if not currently borrowed)
- View books sorted by name or available quantity
- Search books by title, ISBN, or author
- User management (add admins and borrowers)
- Fine limit management for borrowers

### Module C: Borrowing System (Borrowers)
- View available books
- Search books by various criteria
- Shopping cart system for book selection
- Maximum 3 books borrowing limit
- Minimum Rs. 500 security deposit requirement
- Cannot borrow the same book twice
- 15-day borrowing period with extension options

### Module D: Fine and Regulations
- Initial security deposit of Rs. 1500 for borrowers
- Overdue fine: Rs. 2 per day, exponentially increasing every 10 days
- Maximum fine capped at 80% of book cost
- Lost book fine: 50% of book cost
- Lost membership card fine: Rs. 10
- Fine payment options: cash or security deposit deduction
- Maximum 2 consecutive extensions per book
- All borrowed books must be different

### Module E: Reports
#### Admin Reports:
1. Books with low quantity (for restocking)
2. Books never borrowed
3. Most borrowed books (top 10)
4. Outstanding books by date
5. Book status by ISBN (shows borrower details)
6. All fines report

#### Borrower Reports:
1. Personal fine history with payment status
2. Personal borrowing history

## Data Persistence
- All data is saved to text files:
  - `users.txt` - User accounts and details
  - `books.txt` - Book inventory
  - `borrowing.txt` - Borrowing records
  - `fines.txt` - Fine records

## Getting Started

### Prerequisites
- Java 8 or higher
- Windows Command Prompt or PowerShell

### Running the Application

1. **Compile the application:**
   ```bash
   javac LibraryManagementSystem.java
   ```

2. **Run the application:**
   ```bash
   java LibraryManagementSystem
   ```
   
   Or simply double-click `run.bat`

### Default Login Credentials
- **Admin:** admin@library.com / admin123
- The system will create sample books automatically on first run

## Sample Books Included
1. Effective Java by Joshua Bloch (ISBN: 978-0134685991)
2. Head First Design Patterns by Eric Freeman (ISBN: 978-0596009205)
3. Clean Code by Robert Martin (ISBN: 978-0132350884)
4. Effective Java Programming by Joshua Bloch (ISBN: 978-0321356680)
5. Design Patterns by Gang of Four (ISBN: 978-0201633610)

## Usage Instructions

### For Administrators:
1. Login with admin credentials
2. Navigate through the menu to:
   - Manage book inventory
   - Add/modify users
   - View comprehensive reports
   - Manage fines

### For Borrowers:
1. Register through admin or login with borrower credentials
2. Ensure minimum Rs. 500 security deposit
3. Browse and search books
4. Add books to cart and checkout
5. Return books on time to avoid fines
6. View personal reports

## File Structure
```
LibraryManagementSystem.java  # Main application file
run.bat                      # Windows batch file to run the application
users.txt                    # User data storage
books.txt                    # Book inventory storage
borrowing.txt                # Borrowing records storage
fines.txt                    # Fine records storage
README.md                    # This file
```

## Key Business Rules
- Borrowers must maintain minimum Rs. 500 security deposit
- Maximum 3 books can be borrowed simultaneously
- 15-day borrowing period with 2 possible extensions
- Overdue fines calculated with exponential progression
- All borrowed books must be different titles
- Fine payments can be made via cash or security deposit deduction

## Technical Features
- Object-oriented design with proper encapsulation
- Enum-based role and fine reason management
- Date handling with proper formatting
- File-based data persistence
- Input validation and error handling
- Comprehensive menu system
- Sorted display options for better user experience

## Future Enhancements
- Database integration (MySQL/PostgreSQL)
- GUI interface using JavaFX or Swing
- Email notifications for due dates
- Barcode scanning integration
- Advanced search filters
- Book recommendation system
- Digital receipt generation

## Support
For any issues or questions, please refer to the code comments or contact the development team.
