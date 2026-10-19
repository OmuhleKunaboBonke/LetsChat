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

        } else {

            System.out.println("\nPlease correct your registration details "
                    + "before attempting to log in.");
        }

        input.close();
        return;
    }    
    public static void runQuickChat(Scanner input) {
        
 
        System.out.println("\nWelcome to QuickChat.");
 
        // how many messages the user wants to enter
        int limit = 0;
        while (limit <= 0) {
            System.out.print("How many messages do you wish to enter? ");
            String answer = input.nextLine().trim();
            if (answer.matches("\\d+")) {
                limit = Integer.parseInt(answer);
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
            String choice = input.nextLine().trim();
 
            if (choice.equals("1")) {
 
                if (messagesEntered) {
                    System.out.println("You have already entered your " + limit + " message(s).");
                } else {
                    // FOR loop: only the set number of messages can be entered
                    for (int i = 0; i < limit; i++) {
                        System.out.println("\n--- Message " + (i + 1) + " of " + limit + " ---");
                        captureMessage(input, i);
                    }
                    messagesEntered = true;
                    System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                }
 
            } else if (choice.equals("2")) {
                System.out.println("Coming Soon.");
            } else if (choice.equals("3")) {
                running = false;
            } else {
                System.out.println("Please enter 1, 2 or 3.");
            }
        }
    }
 
    // Captures, validates and handles ONE message. i = loop counter (message number).
    private static void captureMessage(Scanner input, int i) {
 
        Message msg = null;
        boolean valid = false;
 
        while (!valid) {
            System.out.print("Recipient cell number: ");
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
            String cellCheck = msg.checkRecipientCell();
            System.out.println(cellCheck);
            valid = cellCheck.startsWith("Cell phone number successfully");
        }
 
        System.out.println("Message sent");
 
        // send, disregard or store?
        String status = "";
        boolean done = false;
        while (!done) {
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            String option = input.nextLine().trim();
 
            int number = 0;
            if (option.equals("1")) number = 1;
            else if (option.equals("2")) number = 2;
            else if (option.equals("3")) number = 3;
 
            status = msg.SentMessage(number);
            System.out.println(status);
            done = number != 0;
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