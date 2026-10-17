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
    private static int totalSent = 0;
    
    private String messageID;
    private int messageNumber;
    private String recipient;
    private String text;
    private String hash;
    
    public Message(int messageNumber, String recipient, String text){
       // random ten-digit number
       Random r = new Random();
       this.messageID = String.format("%010d", (long)(r.nextDouble() * 100000000000L));
       this.messageNumber = messageNumber;
       this.recipient = recipient;
       this.text = text;
       this.hash = createMessageHash();
   
    }
    //Message ID must not exceed 10 characters
    public boolean checkMessageID(){
        return messageID.length() <= 10;
   
    }
    
}
