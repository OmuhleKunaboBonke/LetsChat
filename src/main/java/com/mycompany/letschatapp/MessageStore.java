package com.mycompany.letschatapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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

    /*
     * Reads the stored messages from the JSON file (one JSON array) into the stored array.
     * Every message is 8 quoted strings in a fixed order, so the text is read one quoted
     * string at a time and every 8 become one message:
     * "messageID", <id>, "messageHash", <hash>, "recipient", <number>, "message", <text>
     * (This also reads the older format with one object per line.)
     *
     * The quoted string and escape rules (\\ and \") follow the JSON specification:
     * Bray, T. (ed.) (2017) The JavaScript Object Notation (JSON) Data
     * Interchange Format, RFC 8259. Internet Engineering Task Force (IETF).
     * Available at: https://www.rfc-editor.org/info/std90
     * (Accessed: 17 October 2026).
     */
    public void loadStoredMessages() {
        File file = new File(storeFile);
        if (!file.exists()) {
            return;   // nothing has been stored yet
        }

        try {
            String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            readMessages(json);
        } catch (IOException e) {
            System.out.println("Could not read the stored messages file.");
        }
    }

    // Picks the quoted strings out of the JSON text (handles \\ and \" escapes)
    private void readMessages(String json) {
        String[] group = new String[8];
        int n = 0;
        int i = 0;

        while (i < json.length()) {
            if (json.charAt(i) == '"') {
                StringBuilder value = new StringBuilder();
                i++;
                while (i < json.length() && json.charAt(i) != '"') {
                    char c = json.charAt(i);
                    if (c == '\\' && i + 1 < json.length()) {
                        i++;
                        c = json.charAt(i);
                    }
                    value.append(c);
                    i++;
                }
                group[n++] = value.toString();

                if (n == group.length) {
                    n = 0;
                    if (group[0].equals("messageID") && group[2].equals("messageHash")
                            && group[4].equals("recipient") && group[6].equals("message")) {
                        addStored(new Message(group[1], group[3], group[5], group[7]));
                    }
                }
            }
            i++;
        }
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

    // Searches the message ID array; the same index in the message array is the message.
    // Returns null if the ID is not found.
    public Message findByMessageID(String messageID) {
        for (int i = 0; i < idCount; i++) {
            if (messageIDs[i].equals(messageID)) {
                return allMessages[i];
            }
        }
        return null;
    }

    public String displayByMessageID(String messageID) {
        Message found = findByMessageID(messageID);
        if (found == null) {
            return "Message ID not found.";
        }
        return "Recipient: " + found.getRecipient() + "\nMessage: " + found.getText();
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

        // find the hash in the parallel array
        int position = -1;
        for (int i = 0; i < idCount; i++) {
            if (messageHashes[i].equals(hash)) {
                position = i;
                break;
            }
        }
        if (position < 0) {
            return "Message hash not found.";
        }

        Message msg = allMessages[position];

        // remove it from whichever message array it is in
        int index = indexOfMessage(sentMessages, sentCount, msg);
        if (index >= 0) {
            shiftLeft(sentMessages, index, sentCount);
            sentCount--;
        } else {
            index = indexOfMessage(disregardedMessages, disregardedCount, msg);
            if (index >= 0) {
                shiftLeft(disregardedMessages, index, disregardedCount);
                disregardedCount--;
            } else {
                index = indexOfMessage(storedMessages, storedCount, msg);
                if (index >= 0) {
                    shiftLeft(storedMessages, index, storedCount);
                    storedCount--;
                    rewriteStoreFile();   // so the message does not come back next run
                }
            }
        }

        // remove it from the three parallel arrays
        shiftLeft(allMessages, position, idCount);
        shiftLeft(messageHashes, position, idCount);
        shiftLeft(messageIDs, position, idCount);
        idCount--;

        return "Message: \"" + msg.getText() + "\" successfully deleted.";
    }

    private static int indexOfMessage(Message[] list, int count, Message msg) {
        for (int i = 0; i < count; i++) {
            if (list[i] == msg) {
                return i;
            }
        }
        return -1;
    }

    // Saves the stored array back over the JSON file, inside one JSON array
    private void rewriteStoreFile() {
        StringBuilder out = new StringBuilder();
        if (storedCount == 0) {
            out.append("[]\n");
        } else {
            out.append("[\n");
            for (int i = 0; i < storedCount; i++) {
                out.append("  ").append(storedMessages[i].toJson());
                out.append(i < storedCount - 1 ? ",\n" : "\n");
            }
            out.append("]\n");
        }

        try (FileWriter file = new FileWriter(storeFile, false)) {   // false = overwrite
            file.write(out.toString());
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

    


    

