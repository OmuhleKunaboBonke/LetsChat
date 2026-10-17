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
    private int messageNumber;    //
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
                
                
            
            
            }
            
        
        
        }

    }
  
}
