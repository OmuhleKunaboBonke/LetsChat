/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.letschatapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
    
    public MessageStore(String sender) {
        this(sender, Message.STORE_FILE);
    }
    
    public MessageStore(String sender, String storeFile) {
        this.sender = sender;
        this.storeFile = storeFile;
    }
    
    // ===== 1. POPULATING THE ARRAYS =====

    public void addSent(Message msg) {
        if (sentCount == sentMessages.length) {
            sentMessages = grow(sentMessages);
        }
        sentMessages[sentCount++] = msg;
        registerHashAndID(msg);
    }
    
    public void addDisregarded(Message msg) {
        if (disregardedCount == disregardedMessages.length) {
            disregardedMessages = grow(disregardedMessages);
        }
        disregardedMessages[disregardedCount++] = msg;
        registerHashAndID(msg);
    }
    
    public void addStored(Message msg) {
        if (storedCount == storedMessages.length) {
            storedMessages = grow(storedMessages);
        }
        storedMessages[storedCount++] = msg;
        registerHashAndID(msg);
    }
    public void addMessage(Message msg, String flag) {
        if (flag.equalsIgnoreCase("Sent")) {
            addSent(msg);
        } else if (flag.equalsIgnoreCase("Stored")) {
            addStored(msg);
        } else if (flag.toLowerCase().startsWith("disregard")) {
            addDisregarded(msg);
        } else {
            throw new IllegalArgumentException("Unknown flag: " + flag);
        }
    }
    
    private void registerHashAndID(Message msg) {
        if (idCount == messageHashes.length) {
            messageHashes = grow(messageHashes);
            messageIDs = grow(messageIDs);
        }
        messageHashes[idCount] = msg.getMessageHash();
        messageIDs[idCount] = msg.getMessageID();
        idCount++;
    }
    
    /*
     * Reads the stored messages from the JSON file into the stored array.
     * The file has one JSON object per line (written by Message.toJson()), so each
     * line is read and its quoted strings are picked out in order:
     * "messageID", <id>, "messageHash", <hash>, "recipient", <number>, "message", <text>
     */
    public void loadStoredMessages() {
        File file = new File(storeFile);
        if (!file.exists()) {
            return;   // nothing has been stored yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = readJsonStrings(line);
                if (parts != null) {
                    addStored(new Message(parts[1], parts[3], parts[5], parts[7]));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read the stored messages file.");
        }
    }
    // Picks the first 8 quoted strings out of one line of JSON (handles \\ and \" escapes).
    // Returns null if the line does not have all 8.
    private static String[] readJsonStrings(String line) {
        String[] found = new String[8];
        int n = 0;
        int i = 0;

        while (i < line.length() && n < found.length) {
            if (line.charAt(i) == '"') {
                StringBuilder value = new StringBuilder();
                i++;
                while (i < line.length() && line.charAt(i) != '"') {
                    char c = line.charAt(i);
                    if (c == '\\' && i + 1 < line.length()) {
                        i++;
                        c = line.charAt(i);
                    }
                    value.append(c);
                    i++;
                }
                found[n++] = value.toString();
            }
            i++;
        }
        return n == found.length ? found : null;
    }
}
