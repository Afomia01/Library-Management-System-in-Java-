package com.library.roles;

import com.library.management.Library;
import com.library.management.Book;
import java.util.Scanner;


public class AdminRole implements LibraryUser {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "adminpass";
    private static boolean adminLoggedIn = false;

    // ... rest of the code ...
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

