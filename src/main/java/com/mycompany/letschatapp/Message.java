/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.letschatapp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Student
 */
public class Message {
    // Shared by all messages
    // Text of every message sent so far (built up as a single String)
    private static String allSentMessages = "";
    // How many messages have been sent so far
    private static int totalSent = 0;
    
    private String messageID;      //random 10 digit number
    private int messageNumber;    //loop counter value
    private String recipient;    //cell phone number of the receiver
    private String text;        //actual message
    private String hash;       //e.g. 00:0:HIBOSS
    
    public Message(int messageNumber, String recipient, String text){
       // random ten-digit number
       //[logic of the method] makes a random number between 0 and 9 999 999 999
       Random r = new Random();
       long number = (long)(r.nextDouble() * 100000000000L);
       //[logic] %010d pads with zero so the ID is always 10 digits long
       this.messageID = String.format("%010d", number);
       this.messageNumber = messageNumber;
       this.recipient = recipient;
       this.text = text;
       //the hash needs the ID
       this.hash = createMessageHash();
   
    }

    Message() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    //Message ID must not exceed 10 characters
    public boolean checkMessageID(){
        return messageID.length() <= 10;
   
    }
 
    //[check] Number starts with +27
    public String checkRecipientCell(){
        boolean cell = false;
         
        String phoneRegex = "^\\+27[0-9]{9}$";//code reusablity 

        if (recipient.matches(phoneRegex)){
            cell = true;
            return "Cell phone number successfully added";
        }else {
            return "Cell phone number is incorrectly formatted "
                    + "or does not contain an international code.";
        
        }
    }    
    //Message 00:0:HELLO
    //first two digits if ID + : + message number + : + first word + last word
    public String createMessageHash() {
            String clean = text.trim(); //removes spaces at the start and at the end
            String first;
            String last;
            
            if (clean.indexOf(" ") == -1){
                first = clean;
                last = clean;
            
            }else {
                // first word = everything before the first space
                first = clean.substring(0, clean.indexOf(" "));
                // last word = everything after the last space
                last = clean.substring(clean.lastIndexOf(" ") + 1);
            
            }
             // remove punctuation such as "," or "?" so "tonight?" becomes "tonight"
            first = first.replaceAll("[^A-Za-z0-9]", "");
            last = last.replaceAll("[^A-Za-z0-9]", "");
            // [LOGIC] join the parts and change to CAPITAL LETTERS
            return (messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last)
                    .toUpperCase();
     
        }
    // The user's choice is passed in: 1 = send, 2 = disregard, 3 = store
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
