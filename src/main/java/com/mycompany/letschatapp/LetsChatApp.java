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
        
        System.out.println("Enter your username: ");
        String username = input.nextLine();
        
        System.out.println("Enter your password: ");
        String password = input.nextLine();
        
        System.out.println("Enter South Aftrican cell phone number: ");
        String cellPhoneNumber = input.nextLine();
        
        //Registration object
        Registration registration = new Registration();
        
        // Display registration result
        System.out.println();
        System.out.println(registration.registerUser());
        
        // Only continue to login if registration was successful
        if (registration.checkUserName() && registration.checkPasswordComplexity()
                && registration.checkCellPhoneNumber()) {
            
            System.out.println();
            System.out.println("==== LOGIN ====");
            
            System.out.println("Enter username: ");
            String loginUsername = input.nextLine();
            
            System.out.println("Enter password: ");
            String loginPassword = input.nextLine();
            
            //Create Login object
            Login login = new Login(registration.getUsername(), registration.getPassword(), firstName, lastName);
        
        
        }

    }
}    