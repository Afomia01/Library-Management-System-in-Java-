package com.library.main;

import com.library.management.Library;
import com.library.roles.AdminRole;
import com.library.roles.LibraryRole;
import com.library.roles.LibraryUser;
import com.library.roles.UserRole;
import java.util.Scanner;

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
