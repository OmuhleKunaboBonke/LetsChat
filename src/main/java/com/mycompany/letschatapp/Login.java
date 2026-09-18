/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.letschatapp;

/**
 *
 * @author Student
 */
public class Login { 
    private String registeredUsername;
    private String registeredPassword;
    
    private String firstName;
    private String lastName;
    
    // Constructor
    public Login(String registeredUsername, String registeredPassword, String firstName, String lastName){
        
        this.registeredUsername = registeredUsername;
        this.registeredPassword = registeredPassword;
        this.firstName = firstName;
        this.lastName = lastName;

    }
    
    // Check the username and password entered during login
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        
        if (enteredUsername.equals(registeredUsername) && enteredPassword.equals(registeredPassword)){
            
            return true;
        }else {
            return false;
        
        }
    }
    
    // Return the appropriate login message
    public String returnLoginStatus(boolean loginSuccessful){
        
        if (loginSuccessful){
            
             return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you.";

        } else {

            return "Username or password incorrect, please try again.";

        }
    }
}