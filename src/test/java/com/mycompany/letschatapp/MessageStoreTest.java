/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.letschatapp;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for MessageStore (Part 3), using the test data from the brief.
 *
 * Test data:
 *   Message 1: +27834557896, "Did you get the cake?"            -> Sent
 *   Message 2: +27838884567, "Where are you? ..."               -> Stored
 *   Message 3: +27834484567, "Yohoooo, I am at your gate."      -> Disregard
 *   Message 4: 0838884567,   "It is dinner time!"               -> Sent
 *   Message 5: +27838884567, "Ok, I am leaving without you."    -> Stored
 */
public class MessageStoreTest {

    private static final String SENDER = "kyl_1";

    private static final String TEXT_1 = "Did you get the cake?";
    private static final String TEXT_2
            = "Where are you? You are late! I have asked you to be on time.";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";
    private static final String TEXT_4 = "It is dinner time!";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    private static final String ID_1 = "0000000001";
    private static final String ID_2 = "0000000002";
    private static final String ID_3 = "0000000003";
    private static final String ID_4 = "0838884567"; // the brief searches by this ID
    private static final String ID_5 = "0000000005";

    private static final String HASH_1 = "00:0:DIDCAKE?";
    private static final String HASH_2 = "00:1:WHERETIME.";
    private static final String HASH_3 = "00:2:YOHOOOOGATE.";
    private static final String HASH_4 = "08:3:ITTIME!";
    private static final String HASH_5 = "00:4:OKYOU.";

    private static final String RECIPIENT_1 = "+27834557896";
    private static final String RECIPIENT_2 = "+27838884567";
    private static final String RECIPIENT_3 = "+27834484567";
    private static final String RECIPIENT_4 = "0838884567";
    private static final String RECIPIENT_5 = "+27838884567";

    @TempDir
    Path tempDir;

    private String storeFile;
    private MessageStore store;

    // Populates the arrays from the test data (nothing hard-coded in the store)
    @BeforeEach
    void setUp() {
        storeFile = tempDir.resolve("messages.json").toString();
        store = new MessageStore(SENDER, storeFile);

        store.addMessage(new Message(ID_1, HASH_1, RECIPIENT_1, TEXT_1), "Sent");
        store.addMessage(new Message(ID_2, HASH_2, RECIPIENT_2, TEXT_2), "Stored");
        store.addMessage(new Message(ID_3, HASH_3, RECIPIENT_3, TEXT_3), "Disregard");
        store.addMessage(new Message(ID_4, HASH_4, RECIPIENT_4, TEXT_4), "Sent");
        store.addMessage(new Message(ID_5, HASH_5, RECIPIENT_5, TEXT_5), "Stored");
    }

    private MessageStore emptyStore(String fileName) {
        return new MessageStore(SENDER, tempDir.resolve(fileName).toString());
    }

    // =====================================================================
    // 1. Arrays are populated
    // =====================================================================

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        String[] expected = {"Did you get the cake?", "It is dinner time!"};
        assertArrayEquals(expected, store.getSentMessages());
    }

    @Test
    void messagesAreSplitIntoTheCorrectArrays() {
        assertEquals(2, store.getSentCount());
        assertEquals(2, store.getStoredCount());
        assertEquals(1, store.getDisregardedCount());
    }

    @Test
    void messageHashArrayContainsAllHashes() {
        String[] expected = {HASH_1, HASH_2, HASH_3, HASH_4, HASH_5};
        assertArrayEquals(expected, store.getMessageHashes());
    }

    @Test
    void messageIDArrayContainsAllIDs() {
        String[] expected = {ID_1, ID_2, ID_3, ID_4, ID_5};
        assertArrayEquals(expected, store.getMessageIDs());
    }

    @Test
    void hashAndIDArraysStayParallel() {
        String[] ids = store.getMessageIDs();
        String[] hashes = store.getMessageHashes();

        assertEquals(ids.length, hashes.length);
        assertEquals(ID_4, ids[3]);
        assertEquals(HASH_4, hashes[3]);
    }

    @Test
    void duplicateMessageIsNotAddedTwiceToIDAndHashArrays() {
        store.addSent(new Message(ID_1, HASH_1, RECIPIENT_1, TEXT_1));

        assertEquals(5, store.getMessageIDs().length);
        assertEquals(5, store.getMessageHashes().length);
    }

    @Test
    void arraysGrowWhenFull() {
        MessageStore big = emptyStore("big.json");
        for (int i = 0; i < 25; i++) {
            big.addSent(new Message(String.format("%010d", i),
                    "00:" + i + ":HIBYE", RECIPIENT_1, "Hi bye"));
        }

        assertEquals(25, big.getSentCount());
        assertEquals(25, big.getSentMessages().length);
        assertEquals(25, big.getMessageIDs().length);
        assertEquals(25, big.getMessageHashes().length);
    }

    @Test
    void unknownFlagThrowsException() {
        Message message = new Message("0000000009", "00:9:HIBYE",
                RECIPIENT_1, "Hi bye");
        assertThrows(IllegalArgumentException.class,
                () -> store.addMessage(message, "Unknown"));
    }

    // =====================================================================
    // 2a. Display sender and recipient of all stored messages
    // =====================================================================

    @Test
    void displaySenderAndRecipientOfStoredMessages() {
        String expected
                = "Sender: kyl_1 | Recipient: +27838884567\n"
                + "Sender: kyl_1 | Recipient: +27838884567\n";
        assertEquals(expected, store.displaySenderAndRecipient());
    }

    @Test
    void displaySenderAndRecipientWhenNothingStored() {
        assertEquals("There are no stored messages.",
                emptyStore("empty.json").displaySenderAndRecipient());
    }

    // =====================================================================
    // 2b. Display the longest message
    // =====================================================================

    @Test
    void longestMessageOfTestMessages1To4() {
        assertEquals(TEXT_2, store.longestMessage());
    }

    @Test
    void longestStoredMessage() {
        assertEquals(TEXT_2, store.longestStoredMessage());
    }

    @Test
    void longestStoredMessageWhenNothingStored() {
        assertEquals("There are no stored messages.",
                emptyStore("empty.json").longestStoredMessage());
    }

    @Test
    void longestMessageComesFromSentWhenNothingIsStored() {
        MessageStore sentOnly = emptyStore("sentonly.json");
        sentOnly.addSent(new Message(ID_1, HASH_1, RECIPIENT_1, TEXT_1));
        sentOnly.addSent(new Message(ID_4, HASH_4, RECIPIENT_4, TEXT_4));

        assertEquals(TEXT_1, sentOnly.longestMessage());
    }

    // =====================================================================
    // 2c. Search for a message ID, display recipient and message
    // =====================================================================

    @Test
    void searchForMessageIDReturnsMessage4() {
        Message found = store.findByMessageID("0838884567");

        assertNotNull(found);
        assertEquals("It is dinner time!", found.getText());
    }

    @Test
    void searchForMessageIDDisplaysRecipientAndMessage() {
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                store.displayByMessageID("0838884567"));
    }

    @Test
    void searchForMessageIDFindsStoredMessage() {
        assertEquals("Recipient: +27838884567\nMessage: " + TEXT_2,
                store.displayByMessageID(ID_2));
    }

    @Test
    void searchForMessageIDFindsDisregardedMessage() {
        Message found = store.findByMessageID(ID_3);

        assertNotNull(found);
        assertEquals(TEXT_3, found.getText());
    }

    @Test
    void searchForUnknownMessageID() {
        assertNull(store.findByMessageID("9999999999"));
        assertEquals("Message ID not found.",
                store.displayByMessageID("9999999999"));
    }

    // =====================================================================
    // 2d. Search all messages for a particular recipient
    // =====================================================================

    @Test
    void searchByRecipientReturnsAllMessagesForThatRecipient() {
        String expected = "\"" + TEXT_2 + "\"\n\"" + TEXT_5 + "\"";
        assertEquals(expected, store.searchByRecipient("+27838884567"));
    }

    @Test
    void searchByRecipientIncludesSentMessages() {
        assertEquals("\"" + TEXT_1 + "\"",
                store.searchByRecipient(RECIPIENT_1));
    }

    @Test
    void searchByRecipientIgnoresDisregardedMessages() {
        assertEquals("No messages found for that recipient.",
                store.searchByRecipient(RECIPIENT_3));
    }

    @Test
    void searchByRecipientWithNoMatches() {
        assertEquals("No messages found for that recipient.",
                store.searchByRecipient("+27800000000"));
    }

    // =====================================================================
    // 2e. Delete a message using the message hash
    // =====================================================================

    @Test
    void deleteMessage2UsingItsHash() {
        assertEquals("Message: \"" + TEXT_2 + "\" successfully deleted.",
                store.deleteByHash(HASH_2));
    }

    @Test
    void deletedStoredMessageIsRemovedFromAllArrays() {
        store.deleteByHash(HASH_2);

        assertEquals(1, store.getStoredCount());
        assertEquals(4, store.getMessageIDs().length);
        assertEquals(4, store.getMessageHashes().length);
        assertEquals("Message ID not found.", store.displayByMessageID(ID_2));
        assertEquals("\"" + TEXT_5 + "\"",
                store.searchByRecipient(RECIPIENT_2));
    }

    @Test
    void deletingKeepsRemainingMessagesInOrder() {
        store.deleteByHash(HASH_1);

        String[] expectedHashes = {HASH_2, HASH_3, HASH_4, HASH_5};
        assertArrayEquals(expectedHashes, store.getMessageHashes());
    }

    @Test
    void deleteSentMessageUsingHash() {
        assertEquals("Message: \"" + TEXT_1 + "\" successfully deleted.",
                store.deleteByHash(HASH_1));
        assertEquals(1, store.getSentCount());
    }

    @Test
    void deleteDisregardedMessageUsingHash() {
        assertEquals("Message: \"" + TEXT_3 + "\" successfully deleted.",
                store.deleteByHash(HASH_3));
        assertEquals(0, store.getDisregardedCount());
    }

    @Test
    void deleteUnknownHashChangesNothing() {
        assertEquals("Message hash not found.", store.deleteByHash("99:9:NOPE"));
        assertEquals(2, store.getSentCount());
        assertEquals(2, store.getStoredCount());
        assertEquals(1, store.getDisregardedCount());
    }

    // =====================================================================
    // 2f. Report with the full details of the messages
    // =====================================================================

    @Test
    void sentReportShowsHashRecipientAndMessage() {
        String report = store.sentMessagesReport();

        assertTrue(report.contains(HASH_1));
        assertTrue(report.contains(RECIPIENT_1));
        assertTrue(report.contains(TEXT_1));
        assertTrue(report.contains(HASH_4));
        assertTrue(report.contains(RECIPIENT_4));
        assertTrue(report.contains(TEXT_4));
    }

    @Test
    void sentReportDoesNotShowStoredOrDisregardedMessages() {
        String report = store.sentMessagesReport();

        assertFalse(report.contains(TEXT_2));
        assertFalse(report.contains(TEXT_3));
        assertFalse(report.contains(TEXT_5));
    }

    @Test
    void storedReportShowsFullDetails() {
        String report = store.storedMessagesReport();

        assertTrue(report.contains(HASH_2));
        assertTrue(report.contains(ID_2));
        assertTrue(report.contains(RECIPIENT_2));
        assertTrue(report.contains(TEXT_2));
        assertTrue(report.contains(HASH_5));
        assertTrue(report.contains(ID_5));
        assertTrue(report.contains(TEXT_5));
        assertFalse(report.contains(TEXT_1));
    }

    @Test
    void reportWithNoMessages() {
        MessageStore empty = emptyStore("empty.json");

        assertTrue(empty.sentMessagesReport().contains("There are no messages."));
        assertTrue(empty.storedMessagesReport().contains("There are no messages."));
    }

    // =====================================================================
    // 3. Read the JSON file into the stored messages array
    // =====================================================================

    @Test
    void storingAMessageWritesItToTheJsonFile() throws IOException {
        String json = Files.readString(Path.of(storeFile), StandardCharsets.UTF_8);

        assertTrue(json.contains(TEXT_2));
        assertTrue(json.contains(TEXT_5));
        assertFalse(json.contains(TEXT_1)); // sent messages are not stored
        assertFalse(json.contains(TEXT_3)); // disregarded messages are not stored
    }

    @Test
    void storedMessagesAreReadBackIntoTheArray() {
        MessageStore reloaded = new MessageStore(SENDER, storeFile);
        reloaded.loadStoredMessages();

        assertEquals(2, reloaded.getStoredCount());
        assertEquals(TEXT_2, reloaded.longestStoredMessage());
        assertEquals("\"" + TEXT_2 + "\"\n\"" + TEXT_5 + "\"",
                reloaded.searchByRecipient(RECIPIENT_2));
    }

    @Test
    void readsHandWrittenJsonFile() throws IOException {
        Path file = tempDir.resolve("handwritten.json");
        Files.writeString(file,
                "[\n"
                + "  {\"messageID\": \"1111111111\", \"messageHash\": \"11:0:HIBYE\","
                + " \"recipient\": \"+27830000001\", \"message\": \"Hi, bye\"},\n"
                + "  {\"messageID\": \"2222222222\", \"messageHash\": \"22:1:SAYBYE\","
                + " \"recipient\": \"+27830000002\", \"message\": \"Say \\\"hi\\\" bye\"}\n"
                + "]", StandardCharsets.UTF_8);

        MessageStore loaded = new MessageStore(SENDER, file.toString());
        loaded.loadStoredMessages();

        assertEquals(2, loaded.getStoredCount());
        assertEquals("Recipient: +27830000002\nMessage: Say \"hi\" bye",
                loaded.displayByMessageID("2222222222"));
    }

    @Test
    void readingJsonSkipsObjectsWithMissingFields() throws IOException {
        Path file = tempDir.resolve("incomplete.json");
        Files.writeString(file,
                "[{\"messageID\": \"1111111111\", \"recipient\": \"+27830000001\"}]",
                StandardCharsets.UTF_8);

        MessageStore loaded = new MessageStore(SENDER, file.toString());
        loaded.loadStoredMessages();

        assertEquals(0, loaded.getStoredCount());
    }

    @Test
    void braceInsideMessageTextDoesNotBreakJsonReading() throws IOException {
        Path file = tempDir.resolve("braces.json");
        Files.writeString(file,
                "[{\"messageID\": \"1111111111\", \"messageHash\": \"11:0:HIBYE\","
                + " \"recipient\": \"+27830000001\", \"message\": \"Hi {there} bye\"}]",
                StandardCharsets.UTF_8);

        MessageStore loaded = new MessageStore(SENDER, file.toString());
        loaded.loadStoredMessages();

        assertEquals(1, loaded.getStoredCount());
        assertEquals("Hi {there} bye", loaded.longestStoredMessage());
    }

    @Test
    void deletedMessageIsRemovedFromTheJsonFile() {
        store.deleteByHash(HASH_2);

        MessageStore reloaded = new MessageStore(SENDER, storeFile);
        reloaded.loadStoredMessages();

        assertEquals(1, reloaded.getStoredCount());
        assertEquals(TEXT_5, reloaded.longestStoredMessage());
    }

    @Test
    void loadingCreatesTheFileWhenItIsMissing() {
        Path newFile = tempDir.resolve("new.json");
        MessageStore fresh = new MessageStore(SENDER, newFile.toString());
        fresh.loadStoredMessages();

        assertTrue(Files.exists(newFile));
        assertEquals(0, fresh.getStoredCount());
    }
}