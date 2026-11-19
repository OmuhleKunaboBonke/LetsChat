package com.mycompany.letschatapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Holds sent, stored and disregarded messages in plain arrays, plus
 * parallel arrays of every message hash and message ID.
 * No ArrayList or java.util classes are used.
 */
public class MessageStore {

    private static final int START_SIZE = 10;

    private final String sender;
    private final String storeFile;

    private Message[] sentMessages = new Message[START_SIZE];
    private Message[] storedMessages = new Message[START_SIZE];
    private Message[] disregardedMessages = new Message[START_SIZE];
    private String[] messageHashes = new String[START_SIZE];
    private String[] messageIDs = new String[START_SIZE];

    private int sentCount;
    private int storedCount;
    private int disregardedCount;
    private int idCount; // used by messageHashes and messageIDs

    public MessageStore(String sender) {
        this(sender, Message.STORE_FILE);
    }

    public MessageStore(String sender, String storeFile) {
        this.sender = sender;
        this.storeFile = storeFile;
    }

    // ---------- array helpers ----------

    // Make a bigger array and copy the old items across
    private Message[] grow(Message[] array) {
        Message[] bigger = new Message[array.length * 2];
        for (int i = 0; i < array.length; i++) {
            bigger[i] = array[i];
        }
        return bigger;
    }

    private String[] grow(String[] array) {
        String[] bigger = new String[array.length * 2];
        for (int i = 0; i < array.length; i++) {
            bigger[i] = array[i];
        }
        return bigger;
    }

    // Shift items left to close the gap left by a deleted item
    private void removeAt(Object[] array, int index, int count) {
        for (int i = index; i < count - 1; i++) {
            array[i] = array[i + 1];
        }
        array[count - 1] = null;
    }

    private int indexOfHash(Message[] array, int count, String hash) {
        for (int i = 0; i < count; i++) {
            if (array[i].getMessageHash().equals(hash)) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfID(Message[] array, int count, String id) {
        for (int i = 0; i < count; i++) {
            if (array[i].getMessageID().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    // Record hash and ID in the parallel arrays (skips duplicates)
    private void registerHashAndID(Message message) {
        for (int i = 0; i < idCount; i++) {
            if (messageIDs[i].equals(message.getMessageID())) {
                return;
            }
        }
        if (idCount == messageIDs.length) {
            messageIDs = grow(messageIDs);
            messageHashes = grow(messageHashes);
        }
        messageIDs[idCount] = message.getMessageID();
        messageHashes[idCount] = message.getMessageHash();
        idCount++;
    }

    private void unregisterID(String id) {
        for (int i = 0; i < idCount; i++) {
            if (messageIDs[i].equals(id)) {
                removeAt(messageIDs, i, idCount);
                removeAt(messageHashes, i, idCount);
                idCount--;
                return;
            }
        }
    }

    // ---------- adding ----------

    public void addSent(Message message) {
        if (sentCount == sentMessages.length) {
            sentMessages = grow(sentMessages);
        }
        sentMessages[sentCount++] = message;
        registerHashAndID(message);
    }

    public void addDisregarded(Message message) {
        if (disregardedCount == disregardedMessages.length) {
            disregardedMessages = grow(disregardedMessages);
        }
        disregardedMessages[disregardedCount++] = message;
        registerHashAndID(message);
    }

    private void addToStoredArray(Message message) {
        if (storedCount == storedMessages.length) {
            storedMessages = grow(storedMessages);
        }
        storedMessages[storedCount++] = message;
        registerHashAndID(message);
    }

    public void addStored(Message message) {
        addToStoredArray(message);
        rewriteStoreFile();
    }

    public void addMessage(Message message, String flag) {
        if (flag.equalsIgnoreCase("Sent")) {
            addSent(message);
        } else if (flag.equalsIgnoreCase("Stored")) {
            addStored(message);
        } else if (flag.toLowerCase().startsWith("disregard")) {
            addDisregarded(message);
        } else {
            throw new IllegalArgumentException("Unknown message status.");
        }
    }

    // ---------- JSON loading ----------

    // Reads the stored messages JSON file into the storedMessages array
    public void loadStoredMessages() {
        File file = new File(storeFile);

        if (!file.exists()) {
            try {
                Files.writeString(file.toPath(), "[]", StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println("Could not create the messages file.");
            }
            return;
        }

        try {
            String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);

            storedMessages = new Message[START_SIZE];
            storedCount = 0;

            int position = 0;
            while (position < json.length()) {
                int start = json.indexOf("{", position);
                if (start == -1) {
                    break;
                }

                int end = findObjectEnd(json, start);
                if (end == -1) {
                    System.out.println("The JSON file has an invalid object.");
                    return;
                }

                String object = json.substring(start, end + 1);
                String id = getJsonValue(object, "messageID");
                String hash = getJsonValue(object, "messageHash");
                String recipient = getJsonValue(object, "recipient");
                String text = getJsonValue(object, "message");

                if (id != null && hash != null && recipient != null && text != null) {
                    addToStoredArray(new Message(id, hash, recipient, text));
                }
                position = end + 1;
            }
        } catch (IOException e) {
            System.out.println("Could not read the stored messages file.");
        }
    }

    // TODO: add an attribution comment here (source, author, date accessed)
    // Find the end of a JSON object, respecting quoted braces
    private int findObjectEnd(String json, int start) {
        boolean insideString = false;
        boolean escaped = false;
        int depth = 0;

        for (int i = start; i < json.length(); i++) {
            char character = json.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (character == '\\' && insideString) {
                escaped = true;
                continue;
            }

            if (character == '"') {
                insideString = !insideString;
                continue;
            }

            if (!insideString) {
                if (character == '{') {
                    depth++;
                } else if (character == '}') {
                    depth--;
                    if (depth == 0) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    // Read a value belonging to a JSON key
    private String getJsonValue(String object, String key) {
        String search = "\"" + key + "\"";
        int keyPosition = object.indexOf(search);
        if (keyPosition == -1) {
            return null;
        }

        int colon = object.indexOf(":", keyPosition);
        if (colon == -1) {
            return null;
        }

        int start = object.indexOf("\"", colon);
        if (start == -1) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        boolean escaped = false;

        for (int i = start + 1; i < object.length(); i++) {
            char character = object.charAt(i);

            if (escaped) {
                if (character == 'n') {
                    value.append('\n');
                } else if (character == 'r') {
                    value.append('\r');
                } else if (character == 't') {
                    value.append('\t');
                } else {
                    value.append(character);
                }
                escaped = false;

            } else if (character == '\\') {
                escaped = true;

            } else if (character == '"') {
                return value.toString();

            } else {
                value.append(character);
            }
        }
        return null;
    }

    // ---------- getters (used by the unit tests) ----------

    public String[] getSentMessages() {
        String[] texts = new String[sentCount];
        for (int i = 0; i < sentCount; i++) {
            texts[i] = sentMessages[i].getText();
        }
        return texts;
    }

    public String[] getMessageHashes() {
        String[] hashes = new String[idCount];
        for (int i = 0; i < idCount; i++) {
            hashes[i] = messageHashes[i];
        }
        return hashes;
    }

    public String[] getMessageIDs() {
        String[] ids = new String[idCount];
        for (int i = 0; i < idCount; i++) {
            ids[i] = messageIDs[i];
        }
        return ids;
    }

    public int getSentCount() {
        return sentCount;
    }

    public int getStoredCount() {
        return storedCount;
    }

    public int getDisregardedCount() {
        return disregardedCount;
    }

    // ---------- menu features ----------

    public String displaySenderAndRecipient() {
        if (storedCount == 0) {
            return "There are no stored messages.";
        }
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < storedCount; i++) {
            output.append("Sender: ").append(sender)
                    .append(" | Recipient: ")
                    .append(storedMessages[i].getRecipient())
                    .append("\n");
        }
        return output.toString();
    }

    // Longest stored message
    public String longestStoredMessage() {
        if (storedCount == 0) {
            return "There are no stored messages.";
        }
        return longestOf(storedMessages, storedCount);
    }

    // Longest message across sent and stored messages
    public String longestMessage() {
        String longest = "";
        if (sentCount > 0) {
            longest = longestOf(sentMessages, sentCount);
        }
        if (storedCount > 0) {
            String stored = longestOf(storedMessages, storedCount);
            if (stored.length() > longest.length()) {
                longest = stored;
            }
        }
        return longest;
    }

    private String longestOf(Message[] array, int count) {
        String longest = array[0].getText();
        for (int i = 1; i < count; i++) {
            if (array[i].getText().length() > longest.length()) {
                longest = array[i].getText();
            }
        }
        return longest;
    }

    public Message findByMessageID(String id) {
        int index = indexOfID(sentMessages, sentCount, id);
        if (index != -1) {
            return sentMessages[index];
        }
        index = indexOfID(storedMessages, storedCount, id);
        if (index != -1) {
            return storedMessages[index];
        }
        index = indexOfID(disregardedMessages, disregardedCount, id);
        if (index != -1) {
            return disregardedMessages[index];
        }
        return null;
    }

    public String displayByMessageID(String id) {
        Message found = findByMessageID(id);
        if (found == null) {
            return "Message ID not found.";
        }
        return "Recipient: " + found.getRecipient()
                + "\nMessage: " + found.getText();
    }

    // Messages sent or stored for a recipient, one per line, no trailing newline
    public String searchByRecipient(String recipient) {
        StringBuilder output = new StringBuilder();
        appendRecipientMessages(sentMessages, sentCount, recipient, output);
        appendRecipientMessages(storedMessages, storedCount, recipient, output);

        if (output.length() == 0) {
            return "No messages found for that recipient.";
        }
        return output.toString().trim();
    }

    private void appendRecipientMessages(Message[] array, int count,
            String recipient, StringBuilder output) {
        for (int i = 0; i < count; i++) {
            if (array[i].getRecipient().equals(recipient)) {
                output.append("\"").append(array[i].getText()).append("\"\n");
            }
        }
    }

    // Delete by hash: stored first, then sent, then disregarded
    public String deleteByHash(String hash) {
        String deletedText;
        String id;

        int index = indexOfHash(storedMessages, storedCount, hash);
        if (index != -1) {
            deletedText = storedMessages[index].getText();
            id = storedMessages[index].getMessageID();
            removeAt(storedMessages, index, storedCount);
            storedCount--;
            rewriteStoreFile();
        } else {
            index = indexOfHash(sentMessages, sentCount, hash);
            if (index != -1) {
                deletedText = sentMessages[index].getText();
                id = sentMessages[index].getMessageID();
                removeAt(sentMessages, index, sentCount);
                sentCount--;
            } else {
                index = indexOfHash(disregardedMessages, disregardedCount, hash);
                if (index == -1) {
                    return "Message hash not found.";
                }
                deletedText = disregardedMessages[index].getText();
                id = disregardedMessages[index].getMessageID();
                removeAt(disregardedMessages, index, disregardedCount);
                disregardedCount--;
            }
        }

        unregisterID(id);
        return "Message: \"" + deletedText + "\" successfully deleted.";
    }

    // ---------- reports ----------

    public String sentMessagesReport() {
        return createReport("SENT MESSAGES REPORT", sentMessages, sentCount);
    }

    public String storedMessagesReport() {
        return createReport("STORED MESSAGES REPORT", storedMessages, storedCount);
    }

    private String createReport(String title, Message[] array, int count) {
        StringBuilder output = new StringBuilder();
        output.append("===== ").append(title).append(" =====\n");

        if (count == 0) {
            return output.append("There are no messages.").toString();
        }

        for (int i = 0; i < count; i++) {
            Message message = array[i];
            output.append("\nMessage ").append(i + 1)
                    .append("\nMessage Hash: ").append(message.getMessageHash())
                    .append("\nMessage ID: ").append(message.getMessageID())
                    .append("\nRecipient: ").append(message.getRecipient())
                    .append("\nMessage: ").append(message.getText())
                    .append("\n");
        }
        return output.toString();
    }

    // ---------- JSON saving ----------

    // Rewrites the JSON file from the storedMessages array
    private void rewriteStoreFile() {
        StringBuilder output = new StringBuilder("[\n");

        for (int i = 0; i < storedCount; i++) {
            output.append("  ").append(storedMessages[i].toJson());
            if (i < storedCount - 1) {
                output.append(",");
            }
            output.append("\n");
        }
        output.append("]");

        try (FileWriter file = new FileWriter(storeFile, false)) {
            file.write(output.toString());
        } catch (IOException e) {
            System.out.println("Could not update the stored messages file.");
        }
    }
}