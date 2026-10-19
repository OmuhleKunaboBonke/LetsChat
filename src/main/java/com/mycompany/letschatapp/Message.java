/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.letschatapp;

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
    // [logic] builds a random ten-digit ID, one digit per loop pass
    private static String generateMessageID() {
        String id = "";
        for (int i = 0; i < 10; i++) {
            int digit = (int) (Math.random() * 10);   // 0 to 9
            id = id + digit;
        }
        return id;
    }
    
    //Message ID must not exceed 10 characters
    public boolean checkMessageID(){
        return messageID.length() <= 10;
   
    }
    // [OUTPUT] confirmation that an ID was created
    public String createMessageIDMessage() {
        return "Message ID generated: " + messageID;
    }
    
    //[CHECK] message must be 250 characters or fewer
    public String checkMessageLength() {
        if (text.length() <= 250) {
            return "Message ready to send.";
        }
        int tooMany = text.length() - 250;
        return "Message exceeds 250 characters by " + tooMany
                + "; please reduce the size.";
    }
    
    //[check] Number starts with +27
    public String checkRecipientCell(){
        boolean cell = false;
         
        String phoneRegex = "^\\+27[0-9]{9}$";//code reusablity 

        if (recipient.matches(phoneRegex)){
            cell = true;
            System.out.println("Cell phone number successfully added");
        }else {
            System.out.println("Cell phone number is incorrectly formatted "
                    + "or does not contain an international code.");
        
        }
        
        //Message 00:0:HELLO
        //first two digits if ID + : + message number + : + first word + last word
        public String createMessageHash(){
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
        // 1 = send, 2 = disregard, 3 = store (option is passed in so it can be tested)
        

    }
  
}
