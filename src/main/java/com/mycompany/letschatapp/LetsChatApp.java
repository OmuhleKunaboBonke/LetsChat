/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.letschatapp;

import java.util.Scanner;

/**
 * Console entry point for LetsChatApp: registration, login and QuickChat.
 *
 * @author Student
 */
public class LetsChatApp {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        // ---------- Part 1: Registration ----------
        System.out.println("===== REGISTRATION =====");

        System.out.print("Enter your first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();
        

        System.out.print("Enter your username (must contain '_' and be at most 5 characters): ");
        String username = input.nextLine();

        System.out.print("Enter your password (at least 8 characters, a capital letter, "
                + "a number and a special character): ");
        String password = input.nextLine();

        System.out.print("Enter South African cell phone number (must start with '+27'): ");
        String cellPhoneNumber = input.nextLine();

        Login user = new Login(firstName, lastName, username, password, cellPhoneNumber);

        System.out.println("\n" + user.registerUser());

        // ---------- Login ----------
        if (user.checkUserName() && user.checkPasswordComplexity()
                && user.checkCellPhoneNumber()) {

            System.out.println("\n===== LOGIN =====");

            System.out.print("Enter username: ");
            String enteredUsername = input.nextLine();

            System.out.print("Enter password: ");
            String enteredPassword = input.nextLine();

            boolean loginSuccessful = user.loginUser(enteredUsername, enteredPassword);
            System.out.println(user.returnLoginStatus(loginSuccessful));

            // Messaging is only available after a successful login
            if (loginSuccessful) {
                runQuickChat(input, enteredUsername);
            }

        } else {
            System.out.println("\nPlease correct your registration details "
                    + "before attempting to log in.");
        }

        input.close();
    }

    // ---------- Part 2: QuickChat main menu ----------

    public static void runQuickChat(Scanner input, String sender) {

        System.out.println("\nWelcome to QuickChat.");

        MessageStore store = new MessageStore(sender);
        store.loadStoredMessages();

        int limit = 0;

        while (limit <= 0) {
            System.out.print("How many messages do you wish to enter? ");
            String answer = input.nextLine().trim();

            try {
                limit = Integer.parseInt(answer);
                if (limit <= 0) {
                    System.out.println("Enter a number greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }

        boolean messagesEntered = false;
        boolean running = true;

        while (running) {

            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");

            String choice = input.nextLine().trim();

            if (choice.equals("1")) {

                if (messagesEntered) {
                    System.out.println("You have already entered your "
                            + limit + " message(s).");
                } else {
                    for (int i = 0; i < limit; i++) {
                        System.out.println("\n--- Message " + (i + 1)
                                + " of " + limit + " ---");
                        captureMessage(input, i, store);
                    }

                    messagesEntered = true;
                    System.out.println("\nTotal messages sent: "
                            + Message.returnTotalMessages());
                }

            } else if (choice.equals("2")) {
                System.out.println("Coming Soon.");

            } else if (choice.equals("3")) {
                running = false;
                System.out.println("Thank you for using QuickChat.");

            } else if (choice.equals("4")) {
                runStoredMessagesMenu(input, store);

            } else {
                System.out.println("Please enter 1, 2, 3 or 4.");
            }
        }
    }

    // ---------- Part 3: Stored Messages menu (works with the arrays) ----------

    private static void runStoredMessagesMenu(Scanner input, MessageStore store) {

        boolean back = false;

        while (!back) {
            System.out.println("\n===== STORED MESSAGES =====");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages sent or stored for a recipient");
            System.out.println("5) Delete a message using its message hash");
            System.out.println("6) Display a report of all messages");
            System.out.println("7) Back to main menu");
            System.out.print("Choose an option: ");

            String choice = input.nextLine().trim();

            if (choice.equals("1")) {
                System.out.println(store.displaySenderAndRecipient());

            } else if (choice.equals("2")) {
                System.out.println(store.longestStoredMessage());

            } else if (choice.equals("3")) {
                System.out.print("Enter the message ID: ");
                System.out.println(store.displayByMessageID(input.nextLine().trim()));

            } else if (choice.equals("4")) {
                System.out.print("Enter the recipient's cell number: ");
                System.out.println(store.searchByRecipient(input.nextLine().trim()));

            } else if (choice.equals("5")) {
                System.out.print("Enter the message hash: ");
                System.out.println(store.deleteByHash(input.nextLine().trim()));

            } else if (choice.equals("6")) {
                System.out.println(store.sentMessagesReport());
                System.out.println(store.storedMessagesReport());

            } else if (choice.equals("7")) {
                back = true;

            } else {
                System.out.println("Please enter a number from 1 to 7.");
            }
        }
    }

    // ---------- Capture, validate and handle ONE message (i = loop counter) ----------

    private static void captureMessage(Scanner input, int i, MessageStore store) {

        Message msg = null;
        boolean valid = false;

        while (!valid) {

            System.out.print("Recipient cell number (must begin with +27): ");
            String number = input.nextLine().trim();

            System.out.print("Enter your message: ");
            String text = input.nextLine();

            if (text.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
                continue;
            }

            msg = new Message(i, number, text);

            String lengthCheck = msg.checkMessageLength();
            System.out.println(lengthCheck);

            if (!lengthCheck.equals("Message ready to send.")) {
                continue;
            }

            String cellCheck = msg.checkRecipientCell();
            System.out.println(cellCheck);

            valid = cellCheck.startsWith("Cell phone number successfully");
        }

        System.out.println("\nMessage ID: " + msg.getMessageID());
        System.out.println("Message Hash: " + msg.getMessageHash());

        int option = 0;

        while (option == 0) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");

            String answer = input.nextLine().trim();

            if (answer.equals("1")) {
                option = 1;
            } else if (answer.equals("2")) {
                option = 2;
            } else if (answer.equals("3")) {
                option = 3;
            } else {
                System.out.println("Please enter 1, 2 or 3.");
            }
        }

        System.out.println(msg.SentMessage(option));

        if (option == 1) {
            store.addSent(msg);
            System.out.println("\nFull details of messages sent:");
            System.out.println(Message.printMessages());

        } else if (option == 2) {
            store.addDisregarded(msg);

            System.out.print("Press 0 to delete the message: ");
            if (input.nextLine().trim().equals("0")) {
                System.out.println("Message deleted.");
            }

        } else {
            store.addStored(msg);
        }
    }
}
