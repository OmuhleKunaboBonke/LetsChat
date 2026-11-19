/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.letschatapp;

import java.util.Scanner;

/**
 *
 * @author Student
 */
public class LetsChatApp {

    public static void main(String[] args) {
        
        Scanner input = new Scanner (System.in);
        
        // Registration
        System.out.println("===== REGISTRATION =====");
        
        System.out.println("Enter your first name: ");
        String firstName = input.nextLine();
        
        System.out.println("Enter your last name: ");
        String lastName = input.nextLine();
        
        System.out.println("Enter your username (must contain a '_' ): ");
        String username = input.nextLine();
        
        System.out.println("Enter your password (must contain at least at least 8 chars, a capital letter, a number & : ");
        String password = input.nextLine();
        
        System.out.println("Enter South Aftrican cell phone number (must start with '+27') : ");
        String cellPhoneNumber = input.nextLine();
        
        Login user = new  Login(firstName, lastName, username,password,cellPhoneNumber);

        //Display registration result
        System.out.println("\n" + user.registerUser());

       //Login
        if (user.checkUserName() && user.checkPasswordComplexity() && user.checkCellPhoneNumber()) {

            System.out.println("===== LOGIN =====");

            System.out.print("Enter username: ");
            String enteredUsername = input.nextLine();

            System.out.print("Enter password: ");
            String enteredPassword = input.nextLine();

            // Check login details
            boolean loginSuccessful =
                    user.loginUser(enteredUsername, enteredPassword);

            // Display login status
            System.out.println(
                    user.returnLoginStatus(loginSuccessful)
            );
            // [CHECK] messaging is only available after a successful login
            if (loginSuccessful) {
                runQuickChat(input);
            }

        } else {

            System.out.println("\nPlease correct your registration details "
                    + "before attempting to log in.");
        }

        input.close();
    }
    //Part 2
    public static void runQuickChat(Scanner input) {
        System.out.println("\nWelcome to QuickChat.");
        String sender = null;
        
        // arrays for sent, disregarded and stored messages (stored ones are read from the JSON file)
        MessageStore store = new MessageStore(sender);
        store.loadStoredMessages();
 
        // how many messages the user wants to enter
        int limit = 0;
        while (limit <= 0) {
            System.out.print("How many messages do you wish to enter? ");
           String answer = input.nextLine().trim();
            try {
                limit = Integer.parseInt(answer);
            } catch (NumberFormatException e) {
                limit = 0;
            }
            if (limit <= 0) {
                System.out.println("Please enter a whole number greater than 0.");
            }
        }
 
        boolean messagesEntered = false;
 
        // WHILE loop: the application runs until the user quits
        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            String choice = input.nextLine().trim();
 
            if (choice.equals("1")) {
 
                if (messagesEntered) {
                    System.out.println("You have already entered your " + limit + " message(s).");
                } else {
                    // FOR loop: only the set number of messages can be entered
                    for (int i = 0; i < limit; i++) {
                        System.out.println("\n--- Message " + (i + 1) + " of " + limit + " ---");
                    }
                    messagesEntered = true;
                    System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                }
 
            } else if (choice.equals("2")) {
                System.out.println("Coming Soon.");
            } else if (choice.equals("3")) {
                running = false;
            } else if (choice.equals("4")) {
                runStoredMessagesMenu(input, store);
            }else {
                System.out.println("Please enter 1, 2, 3 or 4.");
            }
        }
    }
    
    // Fourth main menu option: works with the message arrays
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
                String[] found = store.searchByRecipient(input.nextLine().trim());
                if (found.length == 0) {
                    System.out.println("No messages found for that recipient.");
                }
                for (int i = 0; i < found.length; i++) {
                    System.out.println("\"" + found[i] + "\"");
                }

            }else if (choice.equals("5")) {
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
       
 
    // Captures, validates and handles ONE message. i = loop counter (message number).
    private static void captureMessage(Scanner input, int i,  MessageStore store) {
 
        Message msg = null;
        boolean valid = false;
 
        while (!valid) {
            System.out.print("Recipient cell number (must begin with '+27': ");
            String number = input.nextLine().trim();
            System.out.print("Message: ");
            String text = input.nextLine();
 
            if (text.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
                continue;
            }
            // message length is checked here (250 characters maximum)
            if (text.length() > 250) {
                System.out.println("Please enter a message of less than 250 characters.");
                continue;
            }
            msg = new Message(i, number, text);
            
            // message length is checked here (250 characters maximum)
            String lengthCheck = msg.checkMessageLength();
            System.out.println(lengthCheck);
            if (!lengthCheck.equals("Message ready to send.")) {
                continue;
            }
            
            String cellCheck = msg.checkRecipientCell();
            System.out.println(cellCheck);
            valid = cellCheck.startsWith("Cell phone number successfully");
        }
 
        // send, disregard or store?
         int option = 0;
        while (option == 0) {
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            String answer = input.nextLine().trim();

            if (answer.equals("1")) option = 1;
            else if (answer.equals("2")) option = 2;
            else if (answer.equals("3")) option = 3;
            else System.out.println("Please enter 1, 2 or 3.");
        }

        String status = msg.SentMessage(option);
        System.out.println(status);

        // put the message into the array that matches what the user chose
        if (option == 1) {
            store.addSent(msg);
        } else if (option == 2) {
            store.addDisregarded(msg);
        } else {
            store.addStored(msg);
        }

        if (status.equals("Press 0 to delete the message.")) {
            if (input.nextLine().trim().equals("0")) {
                System.out.println("Message deleted.");
            }
        }

        // show the full details of the messages sent so far
        if (status.equals("Message successfully sent.")) {
            System.out.println("\nFull details of messages sent:");
            System.out.println(Message.printMessages());
        }
    }
}