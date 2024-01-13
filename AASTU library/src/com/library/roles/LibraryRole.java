package com.library.roles;

import com.library.management.Library;
import com.library.management.Library;
import java.util.Scanner;

 public abstract class LibraryRole {
     LibraryUser user;

    public LibraryRole(LibraryUser user) {
        this.user = user;
    }

    public void performRoleAction(Library library, Scanner scanner) {
        user.performUserAction(library, scanner);
    }
}
