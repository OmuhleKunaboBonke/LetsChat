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

 

}
