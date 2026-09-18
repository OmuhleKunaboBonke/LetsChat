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
    
    // Variables for the user's registration details
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // Variables used during login
    private String loginUsername;
    private String loginPassword;

    // Constructor
    public Login(String username, String password,
                 String cellPhoneNumber,
                 String firstName, String lastName) {

        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    //Check if the username is correctly formatted
    public boolean checkUserName() {
        
        // Username must contain an underscore
        // Username must be no more than 5 characters
        if (username.contains("_") && username.length() <= 5) {
            return true;
        } else {
            return false;
        }
    }
    

    //Check if the password meets the complexity requirements
    public boolean checkPasswordComplexity() {

        boolean hasCapitalLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        // Password must contain at least 8 characters
        if (password.length() < 8) {
            return false;
        }

        // Check every character in the password
        for (int i = 0; i < password.length(); i++) {

            char character = password.charAt(i);

            // Check for capital letter
            if (Character.isUpperCase(character)) {
                hasCapitalLetter = true;
            }

            // Check for number
            if (Character.isDigit(character)) {
                hasNumber = true;
            }

            // Check for special character
            if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        // All requirements must be met
        if (hasCapitalLetter
                && hasNumber
                && hasSpecialCharacter) {

            return true;

        } else {

            return false;
        }
    }

    //Check cell phone number
    public boolean checkCellPhoneNumber() {

        /*
         * Regular expression:
         * ^\\+27[0-9]{9}$
         *
         * ^       = beginning of the number
         * \\+27   = South African international code (+27)
         * [0-9]   = a number from 0 to 9
         * {9}     = exactly 9 digits
         * $       = end of the number
         *
         * Regex adapted with assistance from OpenAI ChatGPT.
         * OpenAI (2026) ChatGPT. 
         * Accessed 18 September 2026.
         */

        String phonePattern = "^\\+27[0-9]{9}$";

        return cellPhoneNumber.matches(phonePattern);
    }

    //Register User
    public String registerUser() {

        if (!checkUserName()) {

            return "Username is not correctly formatted; please ensure "
                    + "that your username contains an underscore and is "
                    + "no more than five characters in length.";

        }

        if (!checkPasswordComplexity()) {

            return "Password is not correctly formatted; please ensure "
                    + "that the password contains at least eight characters, "
                    + "a capital letter, a number, and a special character.";

        }

        if (!checkCellPhoneNumber()) {

            return "Cell number is incorrectly formatted or does not contain "
                    + "an international code; please correct the number and try again.";

        }

        return "Registration successful.";
    }

    //Check the username and password entered during login
    public boolean loginUser(String loginUsername, String loginPassword) {

        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;

        if (loginUsername.equals(username)
                && loginPassword.equals(password)) {

            return true;

        } else {

            return false;
        }
    }

    //Return appropriate login message
    public String returnLoginStatus(boolean loginSuccessful) {

        if (loginSuccessful) {

            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you.";

        } else {

            return "Username or password incorrect, please try again.";
        }
    }

    //Get username
    public String getUsername() {
        return username;
    }
    
    //Get password
    public String getPassword() {
        return password;
    }
    
    //Get cell phone number
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }
    
    //Get First Name
    public String getFirstName() {
        return firstName;
    }
    
    //Get Last Name
    public String getLastName() {
        return lastName;
    }
    
}
