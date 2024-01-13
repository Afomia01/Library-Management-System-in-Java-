package com.library.management;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> books;
    private static final String BOOKS_FILE_PATH = "books.txt";
    private static final String HISTORY_FILE_PATH = "history.txt";

    // ... rest of the code ...
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

