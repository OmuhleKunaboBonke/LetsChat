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


}

    


    

