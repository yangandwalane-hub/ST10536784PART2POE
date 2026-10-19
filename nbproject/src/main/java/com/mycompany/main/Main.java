import java.util.Scanner;

public class Main {

    static Scanner input = new Scanner(System.in);

    // The registered user's details
    static String registeredUsername;
    static String registeredPassword;

    // Username: exactly 5 characters and contains an underscore
    public static boolean checkUserName(String username) {
        return username.length() == 5 && username.contains("_");
    }

    // Password: 8+ characters, capital, lowercase, number and special character
    public static boolean checkPasswordComplexity(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Cell number: +27 followed by 9 digits
    public static boolean checkCellPhoneNumber(String number) {
        return number.matches("\\+27[0-9]{9}");
    }

    public static String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome, it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // Reads a whole number and asks again if the user types something that is not a number
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // Keeps asking until each detail is valid
    public static void registerUser() {
        String username;

        do {
            System.out.print("Enter your username: ");
            username = input.nextLine();

            if (!checkUserName(username)) {
                System.out.println(
                    "Username is incorrectly formatted. Please try again"
                );
            }
        } while (!checkUserName(username));

        String password;

        do {
            System.out.print("Enter your password: ");
            password = input.nextLine();

            if (!checkPasswordComplexity(password)) {
                System.out.println(
                    "Password is incorrectly formatted. Please try again"
                );
            }
        } while (!checkPasswordComplexity(password));

        String cellPhone;

        do {
            System.out.print("Enter your cellphone number (+27): ");
            cellPhone = input.nextLine();

            if (!checkCellPhoneNumber(cellPhone)) {
                System.out.println(
                    "Cellphone number is incorrectly formatted. Please try again"
                );
            }
        } while (!checkCellPhoneNumber(cellPhone));

        registeredUsername = username;
        registeredPassword = password;

        System.out.println("User registered successfully.");
    }

    // Keeps asking until the login details match
    public static void loginUser() {
        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.print("Enter your username to login: ");
            String username = input.nextLine();

            System.out.print("Enter your password to login: ");
            String password = input.nextLine();

            loggedIn = username.equals(registeredUsername)
                    && password.equals(registeredPassword);

            System.out.println(returnLoginStatus(loggedIn));
        }
    }

    // SEND MESSAGES
    public static void sendMessages() {
        int numMessages;

        do {
            numMessages = readInt(
                "How many messages do you want to enter? "
            );

            if (numMessages <= 0) {
                System.out.println(
                    "Please enter a number greater than 0."
                );
            }
        } while (numMessages <= 0);

        for (int i = 0; i < numMessages; i++) {
            System.out.println(
                "--- Message " + (i + 1) + " of " + numMessages + " ---"
            );

            // Message details entered by the user
            String recipient;

            do {
                System.out.print(
                    "Enter the recipient cell number (with international code): "
                );

                recipient = input.nextLine();

                if (!Message.isRecipientValid(recipient)) {
                    System.out.println(
                        "Cell phone number is incorrectly formatted. Please try again."
                    );
                }
            } while (!Message.isRecipientValid(recipient));

            String text;

            do {
                System.out.print(
                    "Enter your message (max 250 characters): "
                );

                text = input.nextLine();

                System.out.println(Message.checkMessageLength(text));

            } while (!Message.isMessageLengthValid(text));

            // Create the message
            Message message = new Message(i, recipient, text);

            System.out.println(
                "Message ID generated: " + message.getMessageID()
            );

            int choice;

            do {
                System.out.println("1) Send Message");
                System.out.println("2) Disregard Message");
                System.out.println("3) Store Message to send later");

                choice = readInt("Choose an option: ");

                if (choice < 1 || choice > 3) {
                    System.out.println(
                        "Invalid option, please choose 1, 2 or 3."
                    );
                }
            } while (choice < 1 || choice > 3);

            System.out.println(message.sentMessage(choice));

            if (choice == 1) {
                System.out.println(message.getDetails());
            }
        }

        System.out.println(
            "Total number of messages sent: "
                    + Message.returnTotalMessages()
        );
    }

    public static void main(String[] args) {
        registerUser();
        loginUser();

        System.out.println("Welcome to QuickChat.");

        int option = 0;

        while (option != 3) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");

            option = readInt("Choose an option: ");

            if (option == 1) {
                sendMessages();
            } else if (option == 2) {
                System.out.println("Coming Soon.");
            } else if (option != 3) {
                System.out.println(
                    "Invalid option, please choose 1, 2 or 3."
                );
            }
        }

        System.out.println("Goodbye.");
        input.close();
    }
}

// Message class required by the Main class
class Message {

    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageID;

    private static int totalMessages = 0;

    public Message(int messageNumber, String recipient, String messageText) {
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;

        // Generate a 10-digit message ID
        this.messageID = String.format("%010d", messageNumber + 1);
    }

    // Check recipient cellphone number
    public static boolean isRecipientValid(String recipient) {
        return recipient.matches("\\+27[0-9]{9}");
    }

    public String checkRecipientCell() {
        if (isRecipientValid(recipient)) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted.";
    }

    // Check message length
    public static boolean isMessageLengthValid(String text) {
        return text.length() <= 250;
    }

    public static String checkMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        }

        return "Message exceeds 250 characters. Please shorten it.";
    }

    // Return message ID
    public String getMessageID() {
        return messageID;
    }

    // Handle the user's message choice
    public String sentMessage(int choice) {
        if (choice == 1) {
            totalMessages++;
            return "Message successfully sent.";
        } else if (choice == 2) {
            return "Message disregarded.";
        } else {
            return "Message stored to send later.";
        }
    }

    // Display message details
    public String getDetails() {
        return "Message ID: " + messageID
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    // Return total messages sent
    public static int returnTotalMessages() {
        return totalMessages;
    }
}
