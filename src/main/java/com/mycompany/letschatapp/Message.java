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
    
    // File that stored messages are saved to (one JSON object per line)
    public static final String STORE_FILE = "messages.json";
    
    private static final Random r = new Random();
   
 
    private final String messageID;     //random 10 digit number
    private final int messageNumber;     //loop counter value
    private final String recipient;      //cell phone number of the receiver
    private final String text;           //actual message
    private final String hash;           //e.g. 00:0:HIBOSS
    
    // Shared by all messages
    // Text of every message sent so far (built up as a single String)
    private static String allSentMessages = "";
    // How many messages have been sent so far
    private static int totalSent = 0;
    
    // Normal constructor: the message ID is generated automatically
    public Message(int messageNumber, String recipient, String text) {
        this(generateMessageID(), messageNumber, recipient, text);
    }

    // Builds a 10 digit message ID one digit at a time by adding to a String.
    // Leading zeros are allowed, so an ID (and its hash) can start with 00.
    private static String generateMessageID() {
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + r.nextInt(10);
        }
        return id;
    }
    
    // Constructor that takes a message ID (used by the unit tests so hashes are predictable)
    public Message(String messageID, int messageNumber, String recipient, String text){
       
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.hash = createMessageHash();   // needs the ID, so it is created last
        
    }

    // 1. Check message ID
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }
    
    // Used when a stored message is read back from the JSON file,
    // so the original ID and hash are kept exactly as they were saved
    public Message(String messageID, String hash, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = numberFromHash(hash);
        this.recipient = recipient;
        this.text = text;
        this.hash = hash;
    }
    
    // The message number is the middle part of the hash, e.g. 00:0:HITONIGHT gives 0
    private static int numberFromHash(String hash) {
        String[] parts = hash.split(":");
        if (parts.length >= 2) {
            try {
                return Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return hash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    // Reports the generated message ID
    public String displayMessageID() {
        return "Message ID generated: " + messageID;
    }

    // Message must not be more than 250 characters
    public String checkMessageLength() {
        if (text.length() <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (text.length() - 250)
                + "; please reduce the size.";
    }
    
    // 2. Recipient must start with the international code +27 followed by 9 digits
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
        // punctuation is removed, e.g. "tonight?" becomes "tonight"
        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");

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
    
    public String toJson() {
        return "{\"messageID\": \"" + escapeJson(messageID) + "\", "
                + "\"messageHash\": \"" + escapeJson(hash) + "\", "
                + "\"recipient\": \"" + escapeJson(recipient) + "\", "
                + "\"message\": \"" + escapeJson(text) + "\"}";
    }
    
 
    public void storeMessage() {
        try (FileWriter file = new FileWriter(STORE_FILE, true)) {   // true = add to end
            file.write(toJson() + "\n");
        } catch (IOException e) {
            System.out.println("Could not save the message.");
        }
    }

}
