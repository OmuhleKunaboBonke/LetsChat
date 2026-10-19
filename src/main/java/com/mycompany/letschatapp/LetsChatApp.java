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
        
        /*// Registration
        System.out.println("REGISTRATION AND LOGIN");
        
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

            System.out.println(" LOGIN ");

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
    }    
        // Part 2
        
    public static void runLetsChat(Scanner input){*/
        
        //welcome message
        // [OUTPUT] required welcome message
        System.out.println("\nWelcome to QuickChat.");
 
        // [INPUT] how many messages the user wants to enter
        System.out.print("How many messages do you wish to enter? ");
        int limit = Integer.parseInt(input.nextLine());   // text -> whole number
        int count = 0;   // [DATA] how many messages have been entered so far
 
        boolean running = true;   // menu repeats until this becomes false
        while (running) {
            // [OUTPUT] the menu
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            String choice = input.nextLine();
 
            if (choice.equals("1")) {
 
                // [CHECK] stop the user going over the limit they set
                if (count >= limit) {
                    System.out.println("You have already entered your " + limit + " message(s).");
                }
 
                // keep going until the user has entered all their messages
                while (count < limit) {
                    // [INPUT] recipient and message text
                    System.out.print("Recipient cell number: ");
                    String number = input.nextLine();
                    System.out.print("Message: ");
                    String text = input.nextLine();
 
                    // [CHECK] message must not be too long
                    if (text.length() > 250) {
                        System.out.println("Please enter a message of less than 250 characters.");
                        continue;   // ask for this message again
                    }
 
                    // [DATA] create the message (count + 1 = its message number)
                    Message msg = new Message(count + 1, number, text);
 
                    // [CHECK] recipient number must be valid
                    String cellCheck = msg.checkRecipientCell();
                    System.out.println(cellCheck);
                    if (!cellCheck.startsWith("Cell phone number successfully")) {
                        continue;   // ask again
                    }
 
                    // [OUTPUT] message passed the length check
                    System.out.println("Message sent");
 
                    // [INPUT] send, disregard or store?
                    String result = msg.SentMessage(input);
                    System.out.println(result);
 
                    // [OUTPUT] show full details only if it was actually sent
                    if (result.equals("Message successfully sent")) {
                        System.out.println(msg);
                    }
                    count++;   // one more message handled
                }
                // [OUTPUT] running total
                System.out.println("Total messages sent: " + Message.returnTotalMessages());
 
            } else if (choice.equals("2")) {
                // [OUTPUT] feature not built yet
                System.out.println("Coming Soon.");
 
            } else if (choice.equals("3")) {
                running = false;   // leave the menu loop
 
            } else {
                // [CHECK] invalid menu choice
                System.out.println("Please enter 1, 2 or 3.");
            }
        }
 
        // [OUTPUT] everything sent during this run, then the total
        System.out.println(Message.printMessages());
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
    }
}

    

