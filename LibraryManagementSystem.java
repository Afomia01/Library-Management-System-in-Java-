package librarymanagementsystem;

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
            System.out.println("Book borrowed by school ID " + schoolId + ": " + title);
        } else {
            System.out.println("Book not available: " + title);
        }
    }

    public void returnBook(String schoolId) {
        available = true;
        System.out.println("Book returned by school ID " + schoolId + ": " + title);
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Available: " + (available ? "Yes" : "No");
    }
}

class Library {
    private List<Book> books;

    public Library() {
        this.books = new ArrayList<>();

        // Adding sample books
        addSampleBooks();
    }

    private void addSampleBooks() {
        
        addBook(new Book("Macmillan Dictionary", "British Council"));
        addBook(new Book("Object-Oriented Programming", "Jon Doe"));
        addBook(new Book("Economics", "Admas Smith"));
        addBook(new Book("Data Structures and Algorithms", "Michael T. Goodrich"));
    }

    public void addBook(Book book) {
        books.add(book);
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
            System.out.println("1. Add Book");
            System.out.println("2. Check Availability of Books");

            int adminChoice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

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
            System.out.println("1. View Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");

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

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        System.out.println("        AASTU Library Management System      ");
        System.out.println("---------------------------------------------------");

        mainMenu: while (true) {
            System.out.print("Are you an Admin or a User? (admin/user/exit): ");
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









