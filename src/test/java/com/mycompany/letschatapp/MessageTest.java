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
public class MessageTest {
    // MESSAGE LENGTH TESTS
    @Test
    public void testMessageLengthSuccess() {
 
        Message msg = new Message(0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
 
        assertEquals(
                "Message ready to send.",
                msg.text.length()
        );
    }
    
    @Test
    public void testRecipientNumberIncorrectlyFormatted() {
 
        Message msg = new Message(1, "08575975889",
                "Hi Keegan, did you receive the payment?");
 
        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.",
                msg.checkRecipientCell()
        );
    }
    // RECIPIENT NUMBER TESTS
    @Test
    public void testRecipientNumberCorrectlyFormatted() {
 
        Message msg = new Message(0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
 
        assertEquals(
                "Cell phone number successfully captured.",
                msg.checkRecipientCell()
        );
    }
    
    @Test
    public void testMessageLengthFailure() {
 
        // 255 characters, which is 5 over the limit
        Message msg = new Message(0, "+27718693002", "a".repeat(255));
 
        assertEquals(
                "Message exceeds 250 characters by 5; please reduce the size.",
                msg.text.length()
        );
    }
    
   @Test
    void validMessageID() {
        Message message = new Message(
                0, "+27718693002", "Hello there");

        assertTrue(message.checkMessageID());
    }
    
     @Test
    void validRecipientNumber() {
        Message message = new Message(
                0, "+27718693002", "Hello");

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell());
    }
    
     @Test
    void invalidRecipientNumber() {
        Message message = new Message(
                0, "08575975889", "Hello");

        assertTrue(
                message.checkRecipientCell().contains("incorrectly"));
    }

    
}
