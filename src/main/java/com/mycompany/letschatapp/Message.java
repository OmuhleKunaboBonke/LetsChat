package com.mycompany.letschatapp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Student
 */
public class Message {
    private static final Random r = new Random();
   
 
    private String messageID;     //random 10 digit number
    private int messageNumber;     //loop counter value
    private String recipient;      //cell phone number of the receiver
    String text;           //actual message
    private String hash;           //e.g. 00:0:HIBOSS
    
    // Shared by all messages
    // Text of every message sent so far (built up as a single String)
    private static String allSentMessages = "";
    // How many messages have been sent so far
    private static int totalSent = 0;
    
    public Message(int messageNumber, String recipient, String text){
        this.messageID = String.valueOf(1000000000L + r.nextInt(900000000));

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.hash = createMessageHash();   // needs the ID, so it is created last
        
    }

    Message(String id, int i, String recipient, String text) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    // 1. Check message ID
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }
    
    // Recipient must start with the international code +27 followed by 9 digits
    public String checkRecipientCell() {
        String phoneRegex = "^\\+27[0-9]{9}$";
 
        if (recipient.matches(phoneRegex)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }
    
     // 3. Create message hash
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
 
        return (messageID.substring(0, 2) + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    
    
     // 4. Send, store or disregard message
    public String SentMessage(int option) {
        if (option == 1) {
            // keep the full details (ID, Hash, Recipient, Message) of sent messages
            allSentMessages = allSentMessages
                    + "Message ID: " + messageID + "\n"
                    + "Message Hash: " + hash + "\n"
                    + "Recipient: " + recipient + "\n"
                    + "Message: " + text + "\n\n";
            totalSent++;
            return "Message successfully sent.";
        } else if (option == 2) {
            return "Press 0 to delete the message.";
        } else if (option == 3) {
            storeMessage();
            return "Message successfully stored.";
        }
        return "Invalid option, please choose 1, 2 or 3.";
    }
    
    // Returns all the messages sent while the program is running
    public static String printMessages() {
        return allSentMessages;
    }
 
    // Returns the total number of messages sent
    public static int returnTotalMessages() {
        return totalSent;
    }
    
    // Escapes backslashes and quotes so the value is safe inside a JSON string
    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
 
    /*
     * Saves the message to messages.json (one JSON object per line).
     *
     * Bray, T. (ed.) (2017) The JavaScript Object Notation (JSON) Data
     * Interchange Format, RFC 8259. Internet Engineering Task Force (IETF).
     * Available at: https://www.rfc-editor.org/info/std90
     * (Accessed: 17 October 2026).
     */
    public void storeMessage() {
        String json = "{\"messageID\": \"" + messageID + "\", "
                + "\"messageHash\": \"" + hash + "\", "
                + "\"recipient\": \"" + recipient + "\", "
                + "\"message\": \"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
        try {
            FileWriter file = new FileWriter("messages.json", true);   // true = add to end
            file.write(json + "\n");
            file.close();
        } catch (IOException e) {
            System.out.println("Could not save the message.");
        }
    }

}
