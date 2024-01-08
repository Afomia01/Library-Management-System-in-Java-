
package phase2;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface LibraryUser {
    void performUserAction(Library library, Scanner scanner);
}

abstract class LibraryRole {
    protected LibraryUser user;

    public LibraryRole(LibraryUser user) {
        this.user = user;
    }

    public void performRoleAction(Library library, Scanner scanner) {
        user.performUserAction(library, scanner);
    }
}

class Book {
    private String title;
    private String author;
    private boolean available;
    private LocalDate dueDate;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void borrowBook(String schoolId) {
        if (available) {
            available = false;
            dueDate = LocalDate.now().plusWeeks(1); // Set due date to 1 week from today
            System.out.println("Book borrowed by school ID " + schoolId + ": " + title +
                    ". Due Date: " + dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            System.out.println("Book not available: " + title);
        }
    }

    public boolean isReturnedOnTime() {
        return available || LocalDate.now().isBefore(dueDate);
    }

    public double calculateLateFee() {
        if (!available && LocalDate.now().isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            return daysLate * 10.0; // Assuming 10 birr per day as the late fee
        }
        return 0.0;
    }

    public void sendReminderEmail() {
        if (!available && LocalDate.now().plusDays(1).equals(dueDate)) {
            System.out.println("Sending reminder email: The book \"" + title + "\" is due tomorrow.");
            // Add actual email sending logic here
        }
    }

    public void returnBook(String schoolId) {
        if (available) {
            System.out.println("Book not borrowed by school ID " + schoolId + ": " + title);
            return;
        }

        if (isReturnedOnTime()) {
            System.out.println("Book returned on time by school ID " + schoolId + ": " + title);
        } else {
            double lateFee = calculateLateFee();
            System.out.println("Book returned late by school ID " + schoolId + ": " + title +
                    ". Late Fee: " + lateFee + " birr");
        }

        available = true;
        dueDate = null;

        // Send reminder email for the next borrower (if applicable)
        sendReminderEmail();
    }

    public void returnBook(String schoolId, Library library) {
        returnBook(schoolId);
        library.writeBooksToFile();
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Available: " + (available ? "Yes" : "No");
    }
}

class Library {
    private List<Book> books;
    private static final String BOOKS_FILE_PATH = "books.txt";
    private static final String HISTORY_FILE_PATH = "history.txt";

    public Library() {
        this.books = new ArrayList<>();
        readBooksFromFile();
        addSampleBooks();
    }

    private void addSampleBooks() {
        addBook(new Book("Macmillan Dictionary", "British Council"));
        addBook(new Book("Object-Oriented Programming", "Jon Doe"));
        addBook(new Book("Economics", "Admas Smith"));
        addBook(new Book("Data Structures and Algorithms", "Michael T. Goodrich"));
    }

    private void readBooksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0].trim();
                    String author = parts[1].trim();
                    Book book = new Book(title, author);
                    books.add(book);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading books file: " + e.getMessage());
        }
    }

    public void writeBooksToFile() {
        try (FileWriter writer = new FileWriter(BOOKS_FILE_PATH)) {
            for (Book book : books) {
                writer.write(book.getTitle() + ", " + book.getAuthor() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to books file: " + e.getMessage());
        }
    }

    public void addBook(Book book) {
        books.add(book);
        writeBooksToFile();
    }

    public void displayBooks() {
        System.out.println("Library Books:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public Book findBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void displayBorrowedBooks() {
        System.out.println("Borrowed Books with Due Dates:");
        for (Book book : books) {
            if (!book.isAvailable()) {
                System.out.println("Title: " + book.getTitle() +
                        ", Due Date: " + book.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        }
    }

    public void removeBook(Book book) {
        books.remove(book);
        writeBooksToFile();
        System.out.println("Book removed: " + book.getTitle());
    }

    public void displayUserProfile(String schoolId) {
        // Add code to display user profile information
    }

    public void logBorrowEvent(String schoolId, Book book) {
        try (FileWriter writer = new FileWriter(HISTORY_FILE_PATH, true)) {
            writer.write(schoolId + ", " + book.getTitle() + ", " + LocalDate.now() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to history file: " + e.getMessage());
        }
    }

    public boolean isValidSchoolId(String schoolId) {
        // Pattern: 'ETS' + four digits + '/' + two digits
        String pattern = "ETS\\d{4}/\\d{2}";
        return schoolId.matches(pattern);
    }

    public void returnBook(String schoolId, String bookTitle) {
        Book returnBook = findBook(bookTitle);
        if (returnBook != null) {
            returnBook.returnBook(schoolId, this);
        } else {
            System.out.println("Book not found in the library.");
        }
    }
}

class AdminRole implements LibraryUser {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "adminpass";
    private static boolean adminLoggedIn = false;

    @Override
    public void performUserAction(Library library, Scanner scanner) {
        if (!adminLoggedIn) {
            System.out.println("Admin Login");

            if (authenticate(scanner)) {
                System.out.println("Login successful.");
                adminLoggedIn = true;
            } else {
                System.out.println("Login failed. Exiting...");
                return;
            }
        }

        adminMenu: while (true) {
            System.out.println("\t\t1. Add Book");
            System.out.println("\t\t2. Check Availability of Books");
            System.out.println("\t\t3. Remove Book");
            System.out.println("\t\t4. List Borrowed Books with Due Dates");

            int adminChoice = scanner.nextInt();
            scanner.nextLine();

            switch (adminChoice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    Book newBook = new Book(title, author);
                    library.addBook(newBook);
                    System.out.println("Book added successfully.");
                    break;

                case 2:
                    System.out.print("Enter the name of the book you want to check: ");
                    String bookToCheck = scanner.nextLine();
                    Book foundBook = library.findBook(bookToCheck);
                    if (foundBook != null) {
                        System.out.println("Book found: " + foundBook);
                        if (foundBook.isAvailable()) {
                            System.out.println("Book is available for borrowing.");
                        } else {
                            System.out.println("Book is currently not available.");
                        }
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 3:
                    System.out.print("Enter the name of the book you want to remove: ");
                    String bookToRemove = scanner.nextLine();
                    Book removedBook = library.findBook(bookToRemove);
                    if (removedBook != null) {
                        library.removeBook(removedBook);
                        System.out.println("Book removed successfully.");
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 4:
                    library.displayBorrowedBooks();
                    break;

                default:
                    System.out.println("Invalid choice. Exiting...");
                    break adminMenu;
            }

            System.out.print("Do you want to perform another action? (yes/no): ");
            String continueOption = scanner.nextLine().toLowerCase();

            if (continueOption.equals("no")) {
                break;
            } else if (!continueOption.equals("yes")) {
                System.out.println("Invalid input. Exiting...");
                return;
            }
        }
    }

    private boolean authenticate(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }
}

class UserRole implements LibraryUser {
    @Override
    public void performUserAction(Library library, Scanner scanner) {
        userMenu: while (true) {
            System.out.println("User Functionality:");
            System.out.println("\t\t1. View Books");
            System.out.println("\t\t2. Borrow Book");
            System.out.println("\t\t3. Return Book");

            int userChoice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (userChoice) {
                case 1:
                    library.displayBooks();
                    break;

                case 2:
                    System.out.print("Enter your school ID: ");
                    String schoolIdBorrow = scanner.nextLine();

                    if (!library.isValidSchoolId(schoolIdBorrow)) {
                        System.out.println("Invalid school ID format. Please use the pattern 'ETS' + four digits + '/' + two digits.");
                        break;
                    }

                    System.out.print("Enter the name of the book you want to borrow: ");
                    String bookToBorrow = scanner.nextLine();
                    Book borrowBook = library.findBook(bookToBorrow);
                    if (borrowBook != null) {
                        if (borrowBook.isAvailable()) {
                            borrowBook.borrowBook(schoolIdBorrow);
                            library.logBorrowEvent(schoolIdBorrow, borrowBook);
                        } else {
                            System.out.println("Sorry, the book is currently not available.");
                        }
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 3:
                    System.out.print("Enter your school ID: ");
                    String schoolIdReturn = scanner.nextLine();

                    if (!library.isValidSchoolId(schoolIdReturn)) {
                        System.out.println("Invalid school ID format. Please use the pattern 'ETS' + four digits + '/' + two digits.");
                        break;
                    }

                    System.out.print("Enter the name of the book you want to return: ");
                    String bookToReturn = scanner.nextLine();
                    library.returnBook(schoolIdReturn, bookToReturn);
                    break;

                default:
                    System.out.println("Invalid choice. Exiting...");
                    break userMenu;
            }

            System.out.print("Do you want to perform another action? (yes/no): ");
            String continueOption = scanner.nextLine().toLowerCase();

            if (continueOption.equals("no")) {
                break;
            } else if (!continueOption.equals("yes")) {
                System.out.println("Invalid input. Exiting...");
                return;
            }
        }
    }
}

public class Phase2 {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        System.out.println("\t\t\tAASTU Library Management System      ");
        System.out.println("\t\t\t---------------------------------------------------");

        mainMenu: while (true) {
            System.out.print("\t\t\tAre you an Admin or a User? (admin/user/exit): ");
            String role = scanner.nextLine().toLowerCase();

            LibraryUser user;
            LibraryRole libraryRole;

            switch (role) {
                case "admin":
                    user = new AdminRole();
                    libraryRole = new LibraryRole(user) {}; // Anonymous subclass of LibraryRole
                    break;

                case "user":
                    user = new UserRole();
                    libraryRole = new LibraryRole(user) {}; // Anonymous subclass of LibraryRole
                    break;

                case "exit":
                    System.out.println("Exiting...");
                    break mainMenu;

                default:
                    System.out.println("Invalid choice. Exiting...");
                    return;
            }

            libraryRole.performRoleAction(library, scanner);
        }
    }
}



/*
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface LibraryUser {
    void performUserAction(Library library, Scanner scanner);
}

abstract class LibraryRole {
    protected LibraryUser user;

    public LibraryRole(LibraryUser user) {
        this.user = user;
    }

    public void performRoleAction(Library library, Scanner scanner) {
        user.performUserAction(library, scanner);
    }
}

class Book {
    private String title;
    private String author;
    private boolean available;
    private LocalDate dueDate;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void borrowBook(String schoolId) {
        if (available) {
            available = false;
            dueDate = LocalDate.now().plusWeeks(1); // Set due date to 1 week from today
            System.out.println("Book borrowed by school ID " + schoolId + ": " + title +
                    ". Due Date: " + dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            System.out.println("Book not available: " + title);
        }
    }

    public boolean isReturnedOnTime() {
        return available || LocalDate.now().isBefore(dueDate);
    }

    public double calculateLateFee() {
        if (!available && LocalDate.now().isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            return daysLate * 10.0; // Assuming 10 birr per day as the late fee
        }
        return 0.0;
    }

    public void sendReminderEmail() {
        if (!available && LocalDate.now().plusDays(1).equals(dueDate)) {
            System.out.println("Sending reminder email: The book \"" + title + "\" is due tomorrow.");
            // Add actual email sending logic here
        }
    }

    public void returnBook(String schoolId) {
        if (available) {
            System.out.println("Book not borrowed by school ID " + schoolId + ": " + title);
            return;
        }

        if (isReturnedOnTime()) {
            System.out.println("Book returned on time by school ID " + schoolId + ": " + title);
        } else {
            double lateFee = calculateLateFee();
            System.out.println("Book returned late by school ID " + schoolId + ": " + title +
                    ". Late Fee: " + lateFee + " birr");
        }

        available = true;
        dueDate = null;

        // Send reminder email for the next borrower (if applicable)
        sendReminderEmail();
    }

    public void returnBook(String schoolId, Library library) {
        returnBook(schoolId);
        library.writeBooksToFile();
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Available: " + (available ? "Yes" : "No");
    }
}

class Library {
    private List<Book> books;
    private static final String BOOKS_FILE_PATH = "books.txt";
    private static final String HISTORY_FILE_PATH = "history.txt";

    public Library() {
        this.books = new ArrayList<>();
        readBooksFromFile();
        addSampleBooks();
    }

    private void addSampleBooks() {
        addBook(new Book("Macmillan Dictionary", "British Council"));
        addBook(new Book("Object-Oriented Programming", "Jon Doe"));
        addBook(new Book("Economics", "Admas Smith"));
        addBook(new Book("Data Structures and Algorithms", "Michael T. Goodrich"));
    }

    private void readBooksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0].trim();
                    String author = parts[1].trim();
                    Book book = new Book(title, author);
                    books.add(book);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading books file: " + e.getMessage());
        }
    }

    public void writeBooksToFile() {
        try (FileWriter writer = new FileWriter(BOOKS_FILE_PATH)) {
            for (Book book : books) {
                writer.write(book.getTitle() + ", " + book.getAuthor() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to books file: " + e.getMessage());
        }
    }

    public void addBook(Book book) {
        books.add(book);
        writeBooksToFile();
    }

    public void displayBooks() {
        System.out.println("Library Books:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public Book findBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void displayBorrowedBooks() {
        System.out.println("Borrowed Books with Due Dates:");
        for (Book book : books) {
            if (!book.isAvailable()) {
                System.out.println("Title: " + book.getTitle() +
                        ", Due Date: " + book.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        }
    }

    public void removeBook(Book book) {
        books.remove(book);
        writeBooksToFile();
        System.out.println("Book removed: " + book.getTitle());
    }

    public void displayUserProfile(String schoolId) {
        // Add code to display user profile information
    }

    public void logBorrowEvent(String schoolId, Book book) {
        try (FileWriter writer = new FileWriter(HISTORY_FILE_PATH, true)) {
            writer.write(schoolId + ", " + book.getTitle() + ", " + LocalDate.now() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to history file: " + e.getMessage());
        }
    }

    public boolean isValidSchoolId(String schoolId) {
        // Pattern: 'ETS' + four digits + '/' + two digits
        String pattern = "ETS\\d{4}/\\d{2}";
        return schoolId.matches(pattern);
    }
}

class AdminRole implements LibraryUser {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "adminpass";
    private static boolean adminLoggedIn = false;

    @Override
    public void performUserAction(Library library, Scanner scanner) {
        if (!adminLoggedIn) {
            System.out.println("Admin Login");

            if (authenticate(scanner)) {
                System.out.println("Login successful.");
                adminLoggedIn = true;
            } else {
                System.out.println("Login failed. Exiting...");
                return;
            }
        }

        adminMenu: while (true) {
            System.out.println("\t\t1. Add Book");
            System.out.println("\t\t2. Check Availability of Books");
            System.out.println("\t\t3. Remove Book");
            System.out.println("\t\t4. List Borrowed Books with Due Dates");

            int adminChoice = scanner.nextInt();
            scanner.nextLine();

            switch (adminChoice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    Book newBook = new Book(title, author);
                    library.addBook(newBook);
                    System.out.println("Book added successfully.");
                    break;

                case 2:
                    System.out.print("Enter the name of the book you want to check: ");
                    String bookToCheck = scanner.nextLine();
                    Book foundBook = library.findBook(bookToCheck);
                    if (foundBook != null) {
                        System.out.println("Book found: " + foundBook);
                        if (foundBook.isAvailable()) {
                            System.out.println("Book is available for borrowing.");
                        } else {
                            System.out.println("Book is currently not available.");
                        }
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 3:
                    System.out.print("Enter the name of the book you want to remove: ");
                    String bookToRemove = scanner.nextLine();
                    Book removedBook = library.findBook(bookToRemove);
                    if (removedBook != null) {
                        library.removeBook(removedBook);
                        System.out.println("Book removed successfully.");
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 4:
                    library.displayBorrowedBooks();
                    break;

                default:
                    System.out.println("Invalid choice. Exiting...");
                    break adminMenu;
            }

            System.out.print("Do you want to perform another action? (yes/no): ");
            String continueOption = scanner.nextLine().toLowerCase();

            if (continueOption.equals("no")) {
                break;
            } else if (!continueOption.equals("yes")) {
                System.out.println("Invalid input. Exiting...");
                return;
            }
        }
    }

    private boolean authenticate(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }
}

class UserRole implements LibraryUser {
    @Override
    public void performUserAction(Library library, Scanner scanner) {
        userMenu: while (true) {
            System.out.println("User Functionality:");
            System.out.println("\t\t1. View Books");
            System.out.println("\t\t2. Borrow Book");
            System.out.println("\t\t3. Return Book");

            int userChoice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (userChoice) {
                case 1:
                    library.displayBooks();
                    break;

                case 2:
                    System.out.print("Enter your school ID: ");
                    String schoolIdBorrow = scanner.nextLine();
                    System.out.print("Enter the name of the book you want to borrow: ");
                    String bookToBorrow = scanner.nextLine();
                    Book borrowBook = library.findBook(bookToBorrow);
                    if (borrowBook != null) {
                        if (borrowBook.isAvailable()) {
                            borrowBook.borrowBook(schoolIdBorrow);
                            library.logBorrowEvent(schoolIdBorrow, borrowBook);
                        } else {
                            System.out.println("Sorry, the book is currently not available.");
                        }
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 3:
                    System.out.print("Enter your school ID: ");
                    String schoolIdReturn = scanner.nextLine();

                    if (!library.isValidSchoolId(schoolIdReturn)) {
                        System.out.println("Invalid school ID format. Please use the pattern 'ETS' + four digits + '/' + two digits.");
                        break;
                    }

                    System.out.print("Enter the name of the book you want to return: ");
                    String bookToReturn = scanner.nextLine();
                    library.returnBook(schoolIdReturn, bookToReturn);
                    break;

                default:
                    System.out.println("Invalid choice. Exiting...");
                    break userMenu;
            }

            System.out.print("Do you want to perform another action? (yes/no): ");
            String continueOption = scanner.nextLine().toLowerCase();

            if (continueOption.equals("no")) {
                break;
            } else if (!continueOption.equals("yes")) {
                System.out.println("Invalid input. Exiting...");
                return;
            }
        }
    }
}

public class Phase2 {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        System.out.println("\t\t\tAASTU Library Management System      ");
        System.out.println("\t\t\t---------------------------------------------------");

        mainMenu: while (true) {
            System.out.print("\t\t\tAre you an Admin or a User? (admin/user/exit): ");
            String role = scanner.nextLine().toLowerCase();

            LibraryUser user;
            LibraryRole libraryRole;

            switch (role) {
                case "admin":
                    user = new AdminRole();
                    libraryRole = new LibraryRole(user) {}; // Anonymous subclass of LibraryRole
                    break;

                case "user":
                    user = new UserRole();
                    libraryRole = new LibraryRole(user) {}; // Anonymous subclass of LibraryRole
                    break;

                case "exit":
                    System.out.println("Exiting...");
                    break mainMenu;

                default:
                
                    System.out.println("Invalid role selected. Please enter 'admin', 'user', or 'exit'.");
                    continue mainMenu;
            }

            while (true) {
                libraryRole.performRoleAction(library, scanner);
                System.out.println("-------------------------------------------------");
                System.out.print("Do you want to perform another action? (yes/no): ");

                String continueOption = scanner.nextLine().toLowerCase();

                if (continueOption.equals("no")) {
                    break;

                } else if (!continueOption.equals("yes")) {
                    System.out.println("Invalid input. Exiting...");
                    return;
                }
            }
        }

        scanner.close();
    }
}


/*

package phase2;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface LibraryUser {
    void performUserAction(Library library, Scanner scanner);
}

abstract class LibraryRole {
    protected LibraryUser user;

    public LibraryRole(LibraryUser user) {
        this.user = user;
    }

    public void performRoleAction(Library library, Scanner scanner) {
        user.performUserAction(library, scanner);
    }
}

class Book {
    private String title;
    private String author;
    private boolean available;
    private LocalDate dueDate;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void borrowBook(String schoolId) {
        if (available) {
            available = false;
            dueDate = LocalDate.now().plusWeeks(1); // Set due date to 1 week from today
            System.out.println("Book borrowed by school ID " + schoolId + ": " + title +
                    ". Due Date: " + dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            System.out.println("Book not available: " + title);
        }
    }

    public void returnBook(String schoolId) {
        available = true;
        System.out.println("Book returned by school ID " + schoolId + ": " + title);
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Available: " + (available ? "Yes" : "No");
    }
}

class Library {
    private List<Book> books;
    private static final String BOOKS_FILE_PATH = "books.txt";
    private static final String HISTORY_FILE_PATH = "history.txt";

    public Library() {
        this.books = new ArrayList<>();
        readBooksFromFile();
        addSampleBooks();
    }

    private void addSampleBooks() {
        addBook(new Book("Macmillan Dictionary", "British Council"));
        addBook(new Book("Object-Oriented Programming", "Jon Doe"));
        addBook(new Book("Economics", "Admas Smith"));
        addBook(new Book("Data Structures and Algorithms", "Michael T. Goodrich"));
    }

    private void readBooksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0].trim();
                    String author = parts[1].trim();
                    Book book = new Book(title, author);
                    books.add(book);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading books file: " + e.getMessage());
        }
    }

    public void writeBooksToFile() {
        try (FileWriter writer = new FileWriter(BOOKS_FILE_PATH)) {
            for (Book book : books) {
                writer.write(book.getTitle() + ", " + book.getAuthor() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to books file: " + e.getMessage());
        }
    }

    public void addBook(Book book) {
        books.add(book);
        writeBooksToFile();
    }

    public void displayBooks() {
        System.out.println("Library Books:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public Book findBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void displayBorrowedBooks() {
        System.out.println("Borrowed Books with Due Dates:");
        for (Book book : books) {
            if (!book.isAvailable()) {
                System.out.println("Title: " + book.getTitle() +
                        ", Due Date: " + book.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        }
    }

    public void removeBook(Book book) {
        books.remove(book);
        writeBooksToFile();
        System.out.println("Book removed: " + book.getTitle());
    }

    public void displayUserProfile(String schoolId) {
        // Add code to display user profile information
    }

    public void logBorrowEvent(String schoolId, Book book) {
        try (FileWriter writer = new FileWriter(HISTORY_FILE_PATH, true)) {
            writer.write(schoolId + ", " + book.getTitle() + ", " + LocalDate.now() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to history file: " + e.getMessage());
        }
    }
}

class AdminRole implements LibraryUser {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "adminpass";
    private static boolean adminLoggedIn = false;

    @Override
    public void performUserAction(Library library, Scanner scanner) {
        if (!adminLoggedIn) {
            System.out.println("Admin Login");

            if (authenticate(scanner)) {
                System.out.println("Login successful.");
                adminLoggedIn = true;
            } else {
                System.out.println("Login failed. Exiting...");
                return;
            }
        }

        adminMenu: while (true) {
            System.out.println("\t\t1. Add Book");
            System.out.println("\t\t2. Check Availability of Books");
            System.out.println("\t\t3. Remove Book");
            System.out.println("\t\t4. List Borrowed Books with Due Dates");

            int adminChoice = scanner.nextInt();
            scanner.nextLine();

            switch (adminChoice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    Book newBook = new Book(title, author);
                    library.addBook(newBook);
                    System.out.println("Book added successfully.");
                    break;

                case 2:
                    System.out.print("Enter the name of the book you want to check: ");
                    String bookToCheck = scanner.nextLine();
                    Book foundBook = library.findBook(bookToCheck);
                    if (foundBook != null) {
                        System.out.println("Book found: " + foundBook);
                        if (foundBook.isAvailable()) {
                            System.out.println("Book is available for borrowing.");
                        } else {
                            System.out.println("Book is currently not available.");
                        }
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 3:
                    System.out.print("Enter the name of the book you want to remove: ");
                    String bookToRemove = scanner.nextLine();
                    Book removedBook = library.findBook(bookToRemove);
                    if (removedBook != null) {
                        library.removeBook(removedBook);
                        System.out.println("Book removed successfully.");
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 4:
                    library.displayBorrowedBooks();
                    break;

                default:
                    System.out.println("Invalid choice. Exiting...");
                    break adminMenu;
            }

            System.out.print("Do you want to perform another action? (yes/no): ");
            String continueOption = scanner.nextLine().toLowerCase();

            if (continueOption.equals("no")) {
                break;
            } else if (!continueOption.equals("yes")) {
                System.out.println("Invalid input. Exiting...");
                return;
            }
        }
    }

    private boolean authenticate(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }
}

class UserRole implements LibraryUser {
    @Override
    public void performUserAction(Library library, Scanner scanner) {
        userMenu: while (true) {
            System.out.println("User Functionality:");
            System.out.println("\t\t1. View Books");
            System.out.println("\t\t2. Borrow Book");
            System.out.println("\t\t3. Return Book");

            int userChoice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (userChoice) {
                case 1:
                    library.displayBooks();
                    break;

                case 2:
                    System.out.print("Enter your school ID: ");
                    String schoolIdBorrow = scanner.nextLine();
                    System.out.print("Enter the name of the book you want to borrow: ");
                    String bookToBorrow = scanner.nextLine();
                    Book borrowBook = library.findBook(bookToBorrow);
                    if (borrowBook != null) {
                        if (borrowBook.isAvailable()) {
                            borrowBook.borrowBook(schoolIdBorrow);
                            library.logBorrowEvent(schoolIdBorrow, borrowBook);
                        } else {
                            System.out.println("Sorry, the book is currently not available.");
                        }
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                case 3:
                    System.out.print("Enter your school ID: ");
                    String schoolIdReturn = scanner.nextLine();
                    System.out.print("Enter the name of the book you want to return: ");
                    String bookToReturn = scanner.nextLine();
                    Book returnBook = library.findBook(bookToReturn);
                    if (returnBook != null) {
                        returnBook.returnBook(schoolIdReturn);
                    } else {
                        System.out.println("Book not found in the library.");
                    }
                    break;

                default:
                    System.out.println("Invalid choice. Exiting...");
                    break userMenu;
            }

            System.out.print("Do you want to perform another action? (yes/no): ");
            String continueOption = scanner.nextLine().toLowerCase();

            if (continueOption.equals("no")) {
                break;
            } else if (!continueOption.equals("yes")) {
                System.out.println("Invalid input. Exiting...");
                return;
            }
        }
    }
}

public class Phase2 {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        System.out.println("\t\t\tAASTU Library Management System      ");
        System.out.println("\t\t\t---------------------------------------------------");

        mainMenu: while (true) {
            System.out.print("\t\t\tAre you an Admin or a User? (admin/user/exit): ");
            String role = scanner.nextLine().toLowerCase();

            LibraryUser user;
            LibraryRole libraryRole;

            switch (role) {
                case "admin":
                    user = new AdminRole();
                    libraryRole = new LibraryRole(user) {}; // Anonymous subclass of LibraryRole
                    break;

                case "user":
                    user = new UserRole();
                    libraryRole = new LibraryRole(user) {}; // Anonymous subclass of LibraryRole
                    break;

                case "exit":
                    System.out.println("Exiting...");
                    break mainMenu;

                default:
                    System.out.println("Invalid role selected. Please enter 'admin', 'user', or 'exit'.");
                    continue mainMenu;
            }

            while (true) {
                libraryRole.performRoleAction(library, scanner);
                System.out.println("-------------------------------------------------");
                System.out.print("Do you want to perform another action? (yes/no): ");

                String continueOption = scanner.nextLine().toLowerCase();

                if (continueOption.equals("no")) {
                    break;

                } else if (!continueOption.equals("yes")) {
                    System.out.println("Invalid input. Exiting...");
                    return;
                }
            }
        }

        scanner.close();
    }
}
*/