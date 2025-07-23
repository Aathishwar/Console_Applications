# 📚 Library Management System

A **comprehensive console-based** 📟 Library Management System built in **Java** ☕ that supports both **Administrator** 👨‍💼 and **Borrower** 👩‍🎓 roles with secure login, book management, borrowing operations, fine calculations, and detailed reports.

---

## ✨ Features

### 🛡️ Module A: Authentication & Welcome Menu

* 🔐 Email-based login for Admins & Borrowers
* 🧑‍💼 Role-based menus
* 🔑 Secure password verification

---

### 📚 Module B: Book Inventory Management *(Admin Only)*

* ➕ Add books with ISBN, title, author, quantity & cost
* ✏️ Modify book details & stock
* ❌ Delete books (if not borrowed)
* 📊 View books sorted by name or quantity
* 🔍 Search by title, ISBN, or author
* 👥 Manage users (Add admins/borrowers)
* 💰 Set fine/security limits

---

### 📖 Module C: Borrowing System *(Borrowers)*

* 📘 View & search available books
* 🛒 Add to cart and borrow (Max 3 books)
* 💳 Min. ₹500 security deposit required
* 🔁 Borrowing period: 15 days with 2 extensions
* ❗ No duplicate book borrowing

---

### ⚖️ Module D: Fine & Regulations

* 💵 Initial deposit: ₹1500
* ⏱️ Overdue: ₹2/day, **exponentially increasing** every 10 days
* 🔝 Max fine: 80% of book cost
* 📕 Lost book: 50% fine
* 🪪 Lost card: ₹10
* 💳 Fine payment: Cash or from deposit
* 🔂 Max 2 extensions/book
* 🚫 No duplicate titles in cart

---

### 📑 Module E: Reports

#### 📊 Admin Reports:

1. 📉 Low stock books
2. 📦 Never borrowed books
3. 🏆 Top 10 borrowed books
4. 📅 Outstanding by date
5. 🔍 Status by ISBN (borrower info)
6. 💸 All fines report

#### 👤 Borrower Reports:

1. 📜 Personal fine history
2. 📚 Borrowing history

---

## 💾 Data Persistence

All data is saved to `.txt` files:

* `users.txt` – User accounts
* `books.txt` – Book inventory
* `borrowing.txt` – Borrowing records
* `fines.txt` – Fine records

---

## 🚀 Getting Started

### ✅ Prerequisites

* Java 8+ ☕
* Windows Command Prompt / PowerShell

### ▶️ Running the Application

1. **Compile the app:**

   ```bash
   javac LibrarySystem/Main.java
   ```

2. **Run the app:**

   ```bash
   java LibrarySystem/Main
   ```

---

### 🔐 Default Login

* **Admin:** `admin@library.com` / `admin123`
  📘 Sample books auto-loaded on first run

---

## 📦 Sample Books Included

1. **Effective Java** – Joshua Bloch *(ISBN: 978-0134685991)*
2. **Head First Design Patterns** – Eric Freeman *(ISBN: 978-0596009205)*
3. **Clean Code** – Robert Martin *(ISBN: 978-0132350884)*
4. **Effective Java Programming** – Joshua Bloch *(ISBN: 978-0321356680)*
5. **Design Patterns** – Gang of Four *(ISBN: 978-0201633610)*

---

## 👨‍💻 Usage Instructions

### For Admins:

* 🔐 Login as Admin
* 📚 Manage books and users
* 🧾 View reports & manage fines

### For Borrowers:

* 🔐 Login via admin registration
* 💳 Ensure ₹500 min deposit
* 📘 Browse/search & borrow
* ⏱️ Return on time to avoid fines
* 📄 View personal reports

---

## 🗂️ File Structure

```
LibrarySystem/Main.java  # Main application
users.txt                    # Users database
books.txt                    # Book inventory
borrowing.txt                # Borrowing records
fines.txt                    # Fines tracking
README.md                    # This file
```

---

## 📏 Key Business Rules

* ₹500 min deposit required to borrow
* Max 3 books per borrower
* 15-day borrowing period + 2 extensions
* 📈 Overdue fines increase exponentially
* 📚 All books borrowed must be unique
* 💳 Fine payment: Cash or from deposit

---

## ⚙️ Technical Features

* 🧱 Object-Oriented Design (OOP)
* 🧾 Enum for roles & fine reasons
* 🕒 Proper date/time formatting
* 📁 File-based data storage
* ❌ Input validation & error handling
* 🧭 Sorted display for better UX

---

## 🔮 Future Enhancements

* 🗃️ Database support (MySQL/PostgreSQL)
* 🖼️ GUI with JavaFX/Swing
* 📬 Email reminders
* 📷 Barcode scanning
* 🔎 Advanced filters & search
* 🤖 Book recommendation engine
* 🧾 Digital receipts

---

## 🆘 Support

For help, check in-code comments or contact the dev team. 💬

---
