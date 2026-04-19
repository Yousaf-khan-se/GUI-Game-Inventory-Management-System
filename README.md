# Game Inventory Management System

A robust Java desktop application for managing in-game items, weapons, and equipment. This project demonstrates a clean separation between the user interface (Swing) and the data layer (SQL Server) using the **DAO Design Pattern**.

## 🚀 Core Concepts Demonstrated
* **DAO (Data Access Object) Pattern:** Abstracts and encapsulates all access to the data source, providing a loose coupling between the business logic and the database.
* **Custom UI Component Architecture:** Demonstrates deep inheritance by extending standard Swing components (`JButton`, `JPanel`, etc.) to implement a custom, modern dark-theme design.
* **JDBC & Persistence:** Handles live connections to a SQL Server instance, utilizing `PreparedStatement` to prevent SQL injection.
* **Unit Testing:** Includes a comprehensive suite of JUnit tests to validate UI state changes and database interactions.
* **Dynamic Data Filtering:** Uses `TableRowSorter` and `DocumentListener` to implement real-time search and filtering within the inventory table.

## ✨ Features
* **Inventory Management:** Add, remove, and view items with attributes like Name, Power, Weight, and Type (Weapon, Armor, Potion).
* **Database Persistence:** All data is stored in a SQL Server database, ensuring items are saved across application restarts.
* **Search & Filter:** Real-time search bar to quickly find items by name or type.
* **Auto-Equip Logic:** A specialized feature that queries the database for the "Most Powerful" item in each category to automatically equip a character.
* **Responsive UI:** Custom-styled components including rounded panels, anti-aliased text, and interactive button states (hover/pressed).

## 🛠️ Configuration & Setup

### 1. Database Setup
This project uses **Microsoft SQL Server**. You will need to create a database named `scdLab9` and an `items` table using the following schema:
```sql
CREATE TABLE items (
    itemName VARCHAR(255) PRIMARY KEY,
    type VARCHAR(50),
    power INT,
    weight DECIMAL(10, 2)
);
```

### 2. Configure Connection
In `ItemDbDAO.java`, update the connection string to match your local SQL Server instance:
```java
String url = "jdbc:sqlserver://YOUR_SERVER_NAME;databaseName=scdLab9;integratedSecurity=true;";
```

### 3. Prerequisites
* **Java SDK 11+**
* **Microsoft JDBC Driver for SQL Server** (Add the `.jar` to your classpath)
* **JUnit 5** (For running tests)

## 🎮 How to Run
1.  Ensure your SQL Server instance is running.
2.  Compile the project files:
    ```bash
    javac -cp ".;sqljdbc4.jar" Main.java
    ```
3.  Run the application:
    ```bash
    java -cp ".;sqljdbc4.jar" Main
    ```

## 🧪 Testing
The project includes automated tests in `MainTest.java`. These tests cover:
* Adding/Removing items from the GUI.
* Search functionality.
* Consecutive equip logic (ensuring items don't repeat incorrectly).
* Database clearing and synchronization.

To run tests:
```bash
# Using a standard JUnit runner
java -jar junit-platform-console-standalone.jar --class-path . --select-class MainTest
```
