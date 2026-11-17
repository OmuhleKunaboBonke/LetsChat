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
    
    // ===== Read-only views of the arrays (exact size, message text only) =====

    public String[] getSentMessages() {
        return texts(sentMessages, sentCount);
    }

    public String[] getDisregardedMessages() {
        return texts(disregardedMessages, disregardedCount);
    }

    public String[] getStoredMessages() {
        return texts(storedMessages, storedCount);
    }

    public String[] getMessageHashes() {
        return copyOf(messageHashes, idCount);
    }

    public String[] getMessageIDs() {
        return copyOf(messageIDs, idCount);
    }
    
    // ===== 2a. Display the sender and recipient of all stored messages =====

    public String displaySenderAndRecipient() {
        if (storedCount == 0) {
            return "There are no stored messages.";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < storedCount; i++) {
            if (i > 0) {
                out.append("\n");
            }
            out.append("Sender: ").append(sender)
               .append(" | Recipient: ").append(storedMessages[i].getRecipient());
        }
        return out.toString();
    }
    
    // ===== 2b. Display the longest stored message =====

    public String longestStoredMessage() {
        if (storedCount == 0) {
            return "There are no stored messages.";
        }
        Message longest = storedMessages[0];
        for (int i = 1; i < storedCount; i++) {
            if (storedMessages[i].getText().length() > longest.getText().length()) {
                longest = storedMessages[i];
            }
        }
        return longest.getText();
    }

    // ===== 2c. Search for a message ID =====

    // Looks through the sent, disregarded and stored arrays. Returns null if not found.
    public Message findByMessageID(String messageID) {
        Message found = searchID(sentMessages, sentCount, messageID);
        if (found == null) {
            found = searchID(disregardedMessages, disregardedCount, messageID);
        }
        if (found == null) {
            found = searchID(storedMessages, storedCount, messageID);
        }
        return found;
    }

    public String displayByMessageID(String messageID) {
        Message found = findByMessageID(messageID);
        if (found == null) {
            return "Message ID not found.";
        }
        return "Recipient: " + found.getRecipient() + "\nMessage: " + found.getText();
    }

    private static Message searchID(Message[] list, int count, String messageID) {
        for (int i = 0; i < count; i++) {
            if (list[i].getMessageID().equals(messageID)) {
                return list[i];
            }
        }
        return null;
    }
    
     // ===== 2d. Search all the messages sent or stored for a particular recipient =====

    public String[] searchByRecipient(String recipient) {
        int matches = 0;
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].getRecipient().equals(recipient)) {
                matches++;
            }
        }
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].getRecipient().equals(recipient)) {
                matches++;
            }
        }

        String[] result = new String[matches];
        int next = 0;
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].getRecipient().equals(recipient)) {
                result[next++] = sentMessages[i].getText();
            }
        }
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].getRecipient().equals(recipient)) {
                result[next++] = storedMessages[i].getText();
            }
        }
        return result;
    }
    
    // ===== 2e. Delete a message using the message hash =====

    public String deleteByHash(String hash) {
        String deletedText = null;

        int index = indexOfHash(sentMessages, sentCount, hash);
        if (index >= 0) {
            deletedText = sentMessages[index].getText();
            shiftLeft(sentMessages, index, sentCount);
            sentCount--;
        } else {
            index = indexOfHash(disregardedMessages, disregardedCount, hash);
            if (index >= 0) {
                deletedText = disregardedMessages[index].getText();
                shiftLeft(disregardedMessages, index, disregardedCount);
                disregardedCount--;
            } else {
                index = indexOfHash(storedMessages, storedCount, hash);
                if (index >= 0) {
                    deletedText = storedMessages[index].getText();
                    shiftLeft(storedMessages, index, storedCount);
                    storedCount--;
                    rewriteStoreFile();   // so the message does not come back next run
                }
            }
        }
        if (deletedText == null) {
            return "Message hash not found.";
        }

        // keep the hash and ID arrays in step with the message arrays
        for (int i = 0; i < idCount; i++) {
            if (messageHashes[i].equals(hash)) {
                shiftLeft(messageHashes, i, idCount);
                shiftLeft(messageIDs, i, idCount);
                idCount--;
                break;
            }
        }

        return "Message: \"" + deletedText + "\" successfully deleted.";
    }
    
    private static int indexOfHash(Message[] list, int count, String hash) {
        for (int i = 0; i < count; i++) {
            if (list[i].getMessageHash().equals(hash)) {
                return i;
            }
        }
        return -1;
    }

    // Saves the stored array back over the JSON file
    private void rewriteStoreFile() {
        try (FileWriter file = new FileWriter(storeFile, false)) {   // false = overwrite
            for (int i = 0; i < storedCount; i++) {
                file.write(storedMessages[i].toJson() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Could not update the stored messages file.");
        }
    }
    
    // ===== 2f. Display a report with the full details of the messages =====

    public String sentMessagesReport() {
        return report("SENT MESSAGES REPORT", sentMessages, sentCount);
    }

    public String storedMessagesReport() {
        return report("STORED MESSAGES REPORT", storedMessages, storedCount);
    }

    private String report(String title, Message[] list, int count) {
        StringBuilder out = new StringBuilder("===== " + title + " =====\n");
        if (count == 0) {
            out.append("There are no messages.\n");
        }
        for (int i = 0; i < count; i++) {
            out.append("\nMessage ").append(i + 1).append("\n")
               .append("Message Hash: ").append(list[i].getMessageHash()).append("\n")
               .append("Message ID: ").append(list[i].getMessageID()).append("\n")
               .append("Sender: ").append(sender).append("\n")
               .append("Recipient: ").append(list[i].getRecipient()).append("\n")
               .append("Message: ").append(list[i].getText()).append("\n");
        }
        return out.toString();
    }
    
    // ===== Array helpers (plain arrays, no ArrayList) =====

    private static Message[] grow(Message[] old) {
        Message[] bigger = new Message[old.length * 2];
        for (int i = 0; i < old.length; i++) {
            bigger[i] = old[i];
        }
        return bigger;
    }

    private static String[] grow(String[] old) {
        String[] bigger = new String[old.length * 2];
        for (int i = 0; i < old.length; i++) {
            bigger[i] = old[i];
        }
        return bigger;
    }
    
    // Removes the item at index by moving everything after it one place left
    private static void shiftLeft(Object[] list, int index, int count) {
        for (int i = index; i < count - 1; i++) {
            list[i] = list[i + 1];
        }
        list[count - 1] = null;
    }

    private static String[] texts(Message[] list, int count) {
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = list[i].getText();
        }
        return result;
    }

    private static String[] copyOf(String[] list, int count) {
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = list[i];
        }
        return result;
    }
  
}
