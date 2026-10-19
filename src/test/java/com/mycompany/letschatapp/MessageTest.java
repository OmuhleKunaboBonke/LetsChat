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
    public void testStoreMessage() {
 
        Message msg = new Message(0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
 
        // this also adds the message to messages.json
        assertEquals(
                "Message successfully stored.",
                msg.SentMessage(3)
        );
    }
    
    // MESSAGE SENT TESTS
    @Test
    public void testSendMessage() {
 
        Message msg = new Message(0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
 
        assertEquals(
                "Message successfully sent.",
                msg.SentMessage(1)
        );
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
    @Test
    public void testMessageHashesInLoop() {
 
        String[] ids = {"0012345678", "0987654321"};
        String[] recipients = {"+27718693002", "08575975889"};
        String[] texts = {
            "Hi Mike, can you join us for dinner tonight?",
            "Hi Keegan, did you receive the payment?"
        };
        String[] expectedHashes = {"00:0:HITONIGHT", "09:1:HIPAYMENT"};
 
        for (int i = 0; i < texts.length; i++) {
 
            Message msg = new Message(ids[i], i, recipients[i], texts[i]);
 
            assertEquals(
                    expectedHashes[i],
                    msg.createMessageHash(),
                    "Hash for message " + i + " is incorrect"
            );
        }
    }
    // TOTAL MESSAGES SENT TEST
    @Test
    public void testTotalMessagesSent() {
 
        Message message1 = new Message(0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        Message message2 = new Message(1, "08575975889",
                "Hi Keegan, did you receive the payment?");
 
        // the counter is shared by all tests, so start from its current value
        int before = Message.returnTotalMessages();
 
        message1.SentMessage(1);   // Send
        message2.SentMessage(2);   // Discard
 
        // only the sent message is counted
        assertEquals(before + 1, Message.returnTotalMessages());
    }
    
    

}
