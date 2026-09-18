/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.letschatapp;

/**
 *
 * @author Student
 */
class Registration {
    
    //Variables used to store user's registration information
    private String username;
    private String password;
    private String cellPhoneNumber;
    
    //Constructor
    public Registration(String username, String password, String cellPhoneNumber){
         this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        
    }

    Registration() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public boolean checkUserName(){
        // Username must contain an underscore
        // Username must be no more than 5 characters
        if (username.contains("_") && username.length() <= 5){
            return true;
        }else {
            return false;
        }
        
    }
    
    // Check if the password meets the complexity requirements
    public boolean checkPasswordComplexity(){
        
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;
        
        // Password must have at least 8 characters
        for (int i = 0; i < password.length(); i++){
            
            char character = password.charAt(i);
            
        // Check for a capital letter
        if (Character.isUpperCase(character)){
            hasCapital = true;
            
        }
        
        // Check for a number
        if (Character.isDigit(character)){
            hasNumber = true;
            
        }
        
        // Check for a special character
        if (!Character.isLetterOrDigit(character)){
            hasSpecialCharacter = true;
            
        }

    }
        
    // All four requirements must be true
    if (hasCapital && hasNumber && hasSpecialCharacter){
        return true;
    }else {
        return false;
             
        }
    }    
    // Check if the cellphone number is correctly formatted
    public boolean checkCellPhoneNumber(){
        /*
         * Regular expression:
         * +27 followed by 9 digits
         *
         * The \\ is needed because Java uses a backslash
         * as an escape character.
         *
         * Source: Oracle Java Pattern documentation
         * https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html
         */
        
        String phonePattern = "^\\+27[0-9]{9}$";
        
        return cellPhoneNumber.matches(phonePattern);

    }
    
    // Return the appropriate registration message
    public String registerUser(){
        
        if (!checkUserName()){
            
            return "Username is not correctly formatted; please ensure that "
                    + "your username contains an underscore and is no more than "
                    + "five characters in length."; 

        }else if (!checkPasswordComplexity()){
            
            return "Password is not correctly formatted; please ensure that "
                    + "the password contains at least eight characters, a capital "
                    + "letter, a number, and a special character.";

        }else if (!checkCellPhoneNumber()) {

            return "Cell number is incorrectly formatted or does not contain "
                    + "an international code; please correct the number and try again.";

        }else {

            return "Registration successful.";
        }
    
    }
    // Get username
    public String getUsername(){
        return username;
  
    }
    
    // Get password
    public String getPassword(){
        return password;
    
    }
    
    // Get cellphone number
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }
    
}
     
    

