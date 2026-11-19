package com.mycompany.letschatapp;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Student
 */
public class MessageStore {
    
    private final String sender;      // the logged-in user
    private final String storeFile;   // JSON file that stored messages are saved in

    private Message[] sentMessages = new Message[10];
    private int sentCount = 0;

    private Message[] disregardedMessages = new Message[10];
    private int disregardedCount = 0;

    private Message[] storedMessages = new Message[10];
    private int storedCount = 0;

    // Parallel arrays: the same index in all three is the same message
    // (every message that was sent, disregarded or stored is in here)
    private Message[] allMessages = new Message[10];
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

    // Developer entry: adds a message to the array that matches its flag
    // ("Sent", "Stored" or "Disregard"). Nothing is validated or written to a file.
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
            allMessages = grow(allMessages);
            messageHashes = grow(messageHashes);
            messageIDs = grow(messageIDs);
        }
        allMessages[idCount] = msg;
        messageHashes[idCount] = msg.getMessageHash();
        messageIDs[idCount] = msg.getMessageID();
        idCount++;
    }

}

    


    

