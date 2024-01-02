package librarymanagementsystem;

public class lib {
    
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
