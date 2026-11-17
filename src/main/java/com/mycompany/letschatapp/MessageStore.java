/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.letschatapp;

/**
 *
 * @author Student
 * /**
 * Holds the message arrays for Part 3
 * Arrays start small and are grown by copying into a bigger array when full.
 */
public class MessageStore {
    
    private final String sender;      // the logged-in user
    private final String storeFile;   // JSON file that stored messages are saved in
    
    private Message[] sentMessages = new Message[10];
    private int sentCount = 0;
    
    private Message[] disregardedMessages = new Message[10];
    private int disregardedCount = 0;
    
    // Hash and ID of every message, kept in the same order (same index = same message)
    private String[] messageHashes = new String[10];
    private String[] messageIDs = new String[10];
    private int idCount = 0;
    
    
}
