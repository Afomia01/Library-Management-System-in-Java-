package com.library.roles;

import com.library.management.Library;
import java.util.Scanner;

public interface LibraryUser {
    void performUserAction(Library library, Scanner scanner);
}