import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/*
 * Message class for QuickChat.
 * Attribution: this class was written with the help of Claude (Anthropic AI assistant),
 * October 2026. The JSON file is written by hand with simple string building and loops,
 * so no outside library is needed.
 */
public class Message {

    // Arrays that hold the messages (they grow when they get full)
    private static Message[] sentMessages = new Message[10];
    private static int sentCount = 0;

    private static Message[] storedMessages = new Message[10];
    private static int storedCount = 0;

    // File that the stored messages are saved in
    private static String storagePath = "messages.json";

    private static Random random = new Random();

    // Details of this message
    private String messageID;
    private int messageNumber;
    private String recipient;
    private String message;
    private String messageHash;

    // Creates a message with a random ID
    public Message(int messageNumber, String recipient, String message) {
        this(generateMessageID(), messageNumber, recipient, message);
    }

    // Creates a message with a given ID (used for testing)
    public Message(String messageID, int messageNumber, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    // Builds 15 random digits with a loop, then uses substring to keep the first 10
    private static String generateMessageID() {
        String digits = "";
        for (int i = 0; i < 15; i++) {
            digits = digits + random.nextInt(10);
        }
        return digits.substring(0, 10);
    }

    // The message ID must not be more than 10 characters
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // Checks a recipient number: starts with + and is no longer than 14 characters
    public static String checkRecipientNumber(String number) {
        boolean valid = number != null
                && number.matches("^\\+[0-9]+$")
                && number.length() <= 14
                && number.length() > 4;

        if (valid) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    // Checks the recipient of this message
    public String checkRecipientCell() {
        return checkRecipientNumber(recipient);
    }

    // A message may not be longer than 250 characters
    public static boolean isMessageLengthValid(String text) {
        return text != null && text.length() <= 250;
    }

    // Gives the success or failure text for the length check
    public static String checkMessageLength(String text) {
        if (isMessageLengthValid(text)) {
            return "Message ready to send.";
        }
        int extra = text.length() - 250;
        return "Message exceeds 250 characters by " + extra + ", please reduce the size.";
    }

    // Hash = first two digits of ID : message number : FIRSTWORD + LASTWORD (all capitals)
    public String createMessageHash() {
        String firstTwo = messageID.substring(0, 2);

        String[] words = message.trim().split(" ");
        String firstWord = removePunctuation(words[0]);
        String lastWord = removePunctuation(words[words.length - 1]);

        return (firstTwo + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    // Keeps only letters and digits by looping through each character
    private static String removePunctuation(String word) {
        String cleaned = "";
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                cleaned = cleaned + c;
            }
        }
        return cleaned;
    }

    // Send (1), disregard (2) or store (3) the message
    public String sentMessage(int choice) {
        if (choice == 1) {
            if (sentCount == sentMessages.length) {
                sentMessages = makeBigger(sentMessages);
            }
            sentMessages[sentCount] = this;
            sentCount++;
            return "Message successfully sent.";
        } else if (choice == 2) {
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            storeMessage();
            return "Message successfully stored.";
        }
        return "Invalid option.";
    }

    // Makes an array twice as big and copies the old messages across
    private static Message[] makeBigger(Message[] old) {
        Message[] bigger = new Message[old.length * 2];
        for (int i = 0; i < old.length; i++) {
            bigger[i] = old[i];
        }
        return bigger;
    }

    // Returns the details of every sent message
    public static String printMessages() {
        String output = "";
        for (int i = 0; i < sentCount; i++) {
            output = output + sentMessages[i].getDetails() + "\n";
        }
        return output;
    }

    // Returns how many messages have been sent
    public static int returnTotalMessages() {
        return sentCount;
    }

    // Adds this message to the stored array and saves all stored messages as a JSON file
    public boolean storeMessage() {
        if (storedCount == storedMessages.length) {
            storedMessages = makeBigger(storedMessages);
        }
        storedMessages[storedCount] = this;
        storedCount++;

        String json = "[\n";
        for (int i = 0; i < storedCount; i++) {
            json = json + storedMessages[i].toJson();
            if (i < storedCount - 1) {
                json = json + ",";
            }
            json = json + "\n";
        }
        json = json + "]";

        try {
            Files.write(Paths.get(storagePath), json.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
            return false;
        }
    }

    // Turns this message into one JSON object
    private String toJson() {
        return "  {\n"
                + "    \"messageID\": \"" + clean(messageID) + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"recipient\": \"" + clean(recipient) + "\",\n"
                + "    \"message\": \"" + clean(message) + "\",\n"
                + "    \"messageHash\": \"" + clean(messageHash) + "\"\n"
                + "  }";
    }

    // Makes text safe to put inside JSON quotes
    private static String clean(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Full details of this message for display
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + message + "\n";
    }

    // Used by the tests
    public static void clearMessages() {
        sentMessages = new Message[10];
        sentCount = 0;
        storedMessages = new Message[10];
        storedCount = 0;
    }

    public static void setStoragePath(String path) {
        storagePath = path;
    }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
}
