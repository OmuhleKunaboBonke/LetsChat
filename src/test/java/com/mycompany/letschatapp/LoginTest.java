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
    
    //Username Test
    @Test
    public void testUsernameCorrectly(){
        
        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Anele",
                "Mahlayeye");
        
        assertFalse(user.checkUserName());

    }

    
   
}
