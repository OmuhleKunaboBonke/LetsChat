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
        
        Login user = new  Login username, password, cellPhoneNumber, firstName, lastName);

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
}   