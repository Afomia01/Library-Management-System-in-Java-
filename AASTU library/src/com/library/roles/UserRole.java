package com.library.roles;

import com.library.management.Library;
import com.library.management.Book;
import java.util.Scanner;

public class UserRole implements LibraryUser {
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
