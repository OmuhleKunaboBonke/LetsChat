/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.letschatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class LoginTest {
    
     @Test
    public void testUsernameCorrectlyFormatted() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele","Mahlayeye");

        assertTrue(user.checkUserName());
    }
    
    //Username Test
    @Test
    public void testUsernameCorrectly(){
        
        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele", "Mahlayeye");
        
        assertFalse(user.checkUserName());

    }
    
    //Password Test
    @Test
    public void testPasswordCorrectlyFormatted() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele", "Mahlayeye"
        );

        assertTrue(user.checkPasswordComplexity());
    }

    @Test
    public void testPasswordIncorrectlyFormatted() {

        Login user = new Login("kyl_1", "password", "+27838968976", "Anele", "Mahlayeye");

        assertFalse(user.checkPasswordComplexity());
    }
    
     // CELLPHONE TESTS
    @Test
    public void testCellPhoneCorrectlyFormatted() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele", "Mahlayeye");

        assertTrue(user.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "08966553", "Anele", "Mahlayeye");

        assertFalse(user.checkCellPhoneNumber());
    }
    
     // LOGIN TESTS
    @Test
    public void testLoginSuccessful() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele", "Mahlayeye");

        boolean result =
                user.loginUser("kyl_1", "Ch&&sec@ke99!");

        assertTrue(result);
    }

    @Test
    public void testLoginFailed() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele", "Mahlayeye");

        boolean result =
                user.loginUser("wrong_username", "wrong_password");

        assertFalse(result);
    }
    
    // REGISTRATION MESSAGE TEST
    @Test
    public void testPasswordSuccessMessage() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele", "Mahlayeye");

        // Because all registration conditions are correct,
        // registerUser() should return this message.
        assertEquals(
                "Registration successful.",
                user.registerUser()
        );
    }

    @Test
    public void testPasswordFailureMessage() {

        Login user = new Login("kyl_1", "password", "+27838968976", "Anele", "Mahlayeye");

        assertEquals(
                "Password is not correctly formatted; please ensure "
                + "that the password contains at least eight characters, "
                + "a capital letter, a number, and a special character.",
                user.registerUser()
        );
    }
    
    // LOGIN MESSAGE TESTS
     @Test
    public void testSuccessfulLoginMessage() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele", "Mahlayeye");

        boolean loginResult =
                user.loginUser("kyl_1", "Ch&&sec@ke99!");

        assertEquals(
                "Welcome Anele, Mahlayeye it is great to see you.",
                user.returnLoginStatus(loginResult)
        );
    }

    @Test
    public void testFailedLoginMessage() {

        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele", "Mahlayeye");

        boolean loginResult =
                user.loginUser("wrong", "wrong");

        assertEquals(
                "Username or password incorrect, please try again.",
                user.returnLoginStatus(loginResult)
        );
    }

}
