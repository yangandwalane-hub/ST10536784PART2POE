import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    private Message message1;   // Test data 1
    private Message message2;   // Test data 2

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Message.clearMessages();
        Message.setStoragePath(tempDir.resolve("messages.json").toString());
        message1 = new Message("0012345678", 0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        message2 = new Message("0087654321", 1, "08575975889", "Hi Keegan, did you receive the payment?");
    }

    @AfterEach
    void tearDown() {
        Message.clearMessages();
    }

    // ---- Message length ----
    @Test
    void messageLength_success() {
        assertTrue(Message.isMessageLengthValid(message1.getMessage()));
        assertEquals("Message ready to send.", Message.checkMessageLength(message1.getMessage()));
    }

    @Test
    void messageLength_failure() {
        String longMessage = "a".repeat(260);
        assertFalse(Message.isMessageLengthValid(longMessage));
        assertEquals("Message exceeds 250 characters by 10, please reduce the size.",
                Message.checkMessageLength(longMessage));
    }

    // ---- Recipient number ----
    @Test
    void recipientNumber_success() {
        assertEquals("Cell phone number successfully captured.", message1.checkRecipientCell());
    }

    @Test
    void recipientNumber_failure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.", message2.checkRecipientCell());
    }

    // ---- Message hash ----
    @Test
    void messageHash_testCase1() {
        assertEquals("00:0:HITONIGHT", message1.getMessageHash());
    }

    @Test
    void messageHash_testCase2() {
        assertEquals("00:1:HIPAYMENT", message2.getMessageHash());
    }

    // ---- Message ID ----
    @Test
    void messageID_isCreated() {
        assertTrue(message1.checkMessageID());
        assertEquals("0012345678", message1.getMessageID());
    }

    @Test
    void messageID_randomIDIsTenDigits() {
        Message random = new Message(0, "+27718693002", "Hello there");
        assertEquals(10, random.getMessageID().length());
        assertTrue(random.getMessageID().matches("[0-9]{10}"));
    }

    @Test
    void messageID_tooLongFails() {
        Message bad = new Message("12345678901", 0, "+27718693002", "Hello there");
        assertFalse(bad.checkMessageID());
    }

    // ---- Message number made by a loop counter ----
    @Test
    void messageNumber_incrementsInLoop() {
        for (int i = 0; i < 3; i++) {
            Message m = new Message(i, "+27718693002", "Hello there");
            assertEquals(i, m.getMessageNumber());
            assertTrue(m.getMessageHash().startsWith(m.getMessageID().substring(0, 2) + ":" + i + ":"));
        }
    }

    // ---- Send / disregard / store ----
    @Test
    void sentMessage_send() {
        assertEquals("Message successfully sent.", message1.sentMessage(1));
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void sentMessage_disregard() {
        assertEquals("Press 0 to delete the message.", message1.sentMessage(2));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    void sentMessage_store() throws IOException {
        assertEquals("Message successfully stored.", message1.sentMessage(3));
        String json = Files.readString(tempDir.resolve("messages.json"), StandardCharsets.UTF_8);
        assertTrue(json.contains("00:0:HITONIGHT"));
        assertTrue(json.contains("+27718693002"));
    }

    @Test
    void sentMessage_invalidOption() {
        assertEquals("Invalid option.", message1.sentMessage(9));
    }

    // ---- Totals and display ----
    @Test
    void returnTotalMessages_countsSentMessages() {
        message1.sentMessage(1);
        message2.sentMessage(1);
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    void returnTotalMessages_arrayGrowsPastTen() {
        for (int i = 0; i < 25; i++) {
            new Message(i, "+27718693002", "Hello there").sentMessage(1);
        }
        assertEquals(25, Message.returnTotalMessages());
    }

    @Test
    void getDetails_showsAllDetailsInOrder() {
        String expected = "Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: Hi Mike, can you join us for dinner tonight?\n";
        assertEquals(expected, message1.getDetails());
    }

    @Test
    void printMessages_showsAllSentMessages() {
        message1.sentMessage(1);
        message2.sentMessage(1);
        String output = Message.printMessages();
        assertTrue(output.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(output.contains("Message Hash: 00:1:HIPAYMENT"));
    }

    // ---- JSON file keeps earlier messages ----
    @Test
    void storeMessage_keepsAllMessagesInJsonFile() throws IOException {
        assertTrue(message1.storeMessage());
        assertTrue(message2.storeMessage());
        String json = Files.readString(tempDir.resolve("messages.json"), StandardCharsets.UTF_8);
        assertTrue(json.startsWith("["));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("\"messageHash\": \"00:1:HIPAYMENT\""));
    }
}
