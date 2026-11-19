/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.letschatapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class MessageStoreTest {

    // TEST DATA
    // Builds a store from the five test messages (developer entry, so nothing is validated).
    // howMany = how many of the five test messages to load, in order.
    private MessageStore createTestStore(int howMany) {

        MessageStore store = new MessageStore("Anele Mahlayeye", "test_messages.json");

        String[] recipients = {"+27834557896", "+27838884567", "+27834484567",
                               "0838884567", "+27838884567"};
        String[] texts = {
            "Did you get the cake?",
            "Where are you? You are late! I have asked you to be on time.",
            "Yohoooo, I am at your gate.",
            "It is dinner time!",
            "Ok, I am leaving without you."
        };
        String[] flags = {"Sent", "Stored", "Disregard", "Sent", "Stored"};

        for (int i = 0; i < howMany; i++) {
            store.addMessage(new Message(i, recipients[i], texts[i]), flags[i]);
        }
        return store;
    }

    
    // Sent Messages array correctly populated (Test data: message 1-4)
    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {

        MessageStore store = createTestStore(4);

        String[] sent = store.getSentMessages();

        assertEquals(2, sent.length);
        assertEquals("Did you get the cake?", sent[0]);
        assertEquals("It is dinner time!", sent[1]);
    }

    // Display the longest message (Test data: message 1-4)
    @Test
    public void testDisplayLongestMessage() {

        MessageStore store = createTestStore(4);

        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                store.longestStoredMessage()
        );
    }

    // Search for messageID (Test data: message 4)
    @Test
    public void testSearchForMessageID() {

        MessageStore store = createTestStore(4);   // message 4 is the fourth entry

        String messageID = store.getMessageIDs()[3];
        Message found = store.findByMessageID(messageID);

        assertEquals("0838884567", found.getRecipient());
        assertEquals("It is dinner time!", found.getText());
    }

    // Search all the messages sent or stored regarding a particular recipient
    @Test
    public void testSearchMessagesSentOrStoredForParticularRecipient() {

        MessageStore store = createTestStore(5);

        String[] found = store.searchByRecipient("+27838884567");

        assertEquals(2, found.length);
        assertEquals("Where are you? You are late! I have asked you to be on time.", found[0]);
        assertEquals("Ok, I am leaving without you.", found[1]);
    }

    // Delete a message using a message hash (Test data: test message 2)
    @Test
    public void testDeleteMessageUsingMessageHash() throws Exception {

        MessageStore store = createTestStore(5);

        String hashOfMessage2 = store.getMessageHashes()[1];

        assertEquals(
                "Message: \"Where are you? You are late! I have asked you to be on time.\" "
                + "successfully deleted.",
                store.deleteByHash(hashOfMessage2)
        );

        // deleting a stored message rewrites the JSON array, so check it and remove the test file
        String content = new String(Files.readAllBytes(new File("test_messages.json").toPath()),
                StandardCharsets.UTF_8);
        new File("test_messages.json").delete();

        assertTrue(content.trim().startsWith("["));
        assertTrue(content.contains("Ok, I am leaving without you."));
        assertFalse(content.contains("Where are you?"));

        // the message is gone from the stored array and from the parallel arrays
        assertEquals("Ok, I am leaving without you.", store.longestStoredMessage());
        assertEquals(4, store.getMessageHashes().length);
        assertEquals("Message hash not found.", store.deleteByHash(hashOfMessage2));
    }

    // Display report: all the sent messages with Message Hash, Recipient and Message
    @Test
    public void testDisplayReport() {

        MessageStore store = createTestStore(5);

        String report = store.sentMessagesReport();

        // message 1 (Sent)
        assertTrue(report.contains("Message Hash: " + store.getMessageHashes()[0]));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));

        // message 4 (Sent)
        assertTrue(report.contains("Message Hash: " + store.getMessageHashes()[3]));
        assertTrue(report.contains("Recipient: 0838884567"));
        assertTrue(report.contains("Message: It is dinner time!"));

        // a stored message must not be in the sent report
        assertFalse(report.contains("Where are you?"));
    }


    // ARRAY POPULATION TESTS
    @Test
    public void testDisregardedMessagesArrayCorrectlyPopulated() {

        MessageStore store = createTestStore(5);

        String[] disregarded = store.getDisregardedMessages();

        assertEquals(1, disregarded.length);
        assertEquals("Yohoooo, I am at your gate.", disregarded[0]);
    }

    @Test
    public void testStoredMessagesArrayCorrectlyPopulated() {

        MessageStore store = createTestStore(5);

        String[] stored = store.getStoredMessages();

        assertEquals(2, stored.length);
        assertEquals("Where are you? You are late! I have asked you to be on time.", stored[0]);
        assertEquals("Ok, I am leaving without you.", stored[1]);
    }

    @Test
    public void testMessageHashAndIDArraysCorrectlyPopulated() {

        MessageStore store = createTestStore(5);

        String[] hashes = store.getMessageHashes();
        String[] ids = store.getMessageIDs();

        assertEquals(5, hashes.length);
        assertEquals(5, ids.length);

        // message 1 is number 0: first two digits of its ID, then :0:DIDCAKE
        assertEquals(ids[0].substring(0, 2) + ":0:DIDCAKE", hashes[0]);
    }

    @Test
    public void testStoredMessagesReadFromJsonFile() throws Exception {

        // write two messages inside a JSON array (one has quotes in it), then read them back
        Message first = new Message(1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        Message second = new Message(4, "+27838884567", "Say \"hello\" to him.");

        try (FileWriter file = new FileWriter("test_messages.json")) {
            file.write("[\n  " + first.toJson() + ",\n  " + second.toJson() + "\n]\n");
        }

        MessageStore store = new MessageStore("Anele Mahlayeye", "test_messages.json");
        store.loadStoredMessages();
        new File("test_messages.json").delete();

        String[] stored = store.getStoredMessages();

        assertEquals(2, stored.length);
        assertEquals("Where are you? You are late! I have asked you to be on time.", stored[0]);
        assertEquals("Say \"hello\" to him.", stored[1]);
        assertEquals(first.getMessageHash(), store.getMessageHashes()[0]);
        assertEquals(second.getMessageID(), store.getMessageIDs()[1]);
    }

    // SENDER AND RECIPIENT TEST
    @Test
    public void testDisplaySenderAndRecipientOfStoredMessages() {

        MessageStore store = createTestStore(5);

        assertEquals(
                "Sender: Anele Mahlayeye | Recipient: +27838884567\n"
                + "Sender: Anele Mahlayeye | Recipient: +27838884567",
                store.displaySenderAndRecipient()
        );
    }

    // MESSAGE ID SEARCH TESTS
    @Test
    public void testSearchForMessageIDOfDisregardedMessage() {

        MessageStore store = createTestStore(5);   // message 3 was disregarded

        String messageID = store.getMessageIDs()[2];

        assertEquals(
                "Recipient: +27834484567\nMessage: Yohoooo, I am at your gate.",
                store.displayByMessageID(messageID)
        );
    }

    @Test
    public void testSearchForMessageIDNotFound() {

        MessageStore store = createTestStore(4);

        assertEquals("Message ID not found.", store.displayByMessageID("0000000000"));
    }

    // DELETE TEST FOR A SENT MESSAGE
    @Test
    public void testDeleteSentMessageUsingMessageHash() {

        MessageStore store = createTestStore(5);

        String hashOfMessage1 = store.getMessageHashes()[0];

        assertEquals(
                "Message: \"Did you get the cake?\" successfully deleted.",
                store.deleteByHash(hashOfMessage1)
        );

        // gone from the sent array and from both parallel arrays, but nothing else is touched
        assertEquals(1, store.getSentMessages().length);
        assertEquals("It is dinner time!", store.getSentMessages()[0]);
        assertEquals(4, store.getMessageHashes().length);
        assertEquals(4, store.getMessageIDs().length);
        assertEquals(2, store.getStoredMessages().length);

        // a sent message is not in the JSON file, so no file should have been written
        assertFalse(new File("test_messages.json").exists());
    }

    // STORED MESSAGES REPORT TEST
    @Test
    public void testDisplayStoredMessagesReport() {

        MessageStore store = createTestStore(5);

        String report = store.storedMessagesReport();

        assertTrue(report.contains("Message Hash: " + store.getMessageHashes()[1]));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains("Message: Ok, I am leaving without you."));

        // a sent message must not be in the stored report
        assertFalse(report.contains("Did you get the cake?"));
    }

}
