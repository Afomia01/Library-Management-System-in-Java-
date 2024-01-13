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



public class Book {
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


