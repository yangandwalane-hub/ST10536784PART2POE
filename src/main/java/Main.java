import java.util.Scanner;

/*
 * QuickChat: registration, login and messaging.
 * Attribution: written with the help of Claude (Anthropic AI assistant), October 2026.
 */
public class Main {

    static Scanner input = new Scanner(System.in);

    // Details the user typed in
    static String username;
    static String password;
    static String cellPhone;

    // Details that were registered successfully
    static String registeredUsername;
    static String registeredPassword;
    static String registeredCellPhone;

    // Username: exactly 5 characters and contains an underscore
    public static boolean checkUserName(String username) {
        return username.length() == 5 && username.contains("_");
    }

    // Password: 8+ characters with capital, small letter, number and special character
    public static boolean checkPasswordComplexity(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Cell number: +27 followed by exactly 9 digits
    public static boolean checkCellPhoneNumber(String number) {
        return number.matches("^\\+27[0-9]{9}$");
    }

    // Registration: keeps asking until each detail is valid
    public static void registerUser() {
        while (true) {
            System.out.print("Enter your username: ");
            username = input.nextLine();
            if (checkUserName(username)) {
                break;
            }
            System.out.println("Username is incorrectly formatted. Please try again");
        }

        while (true) {
            System.out.print("Enter your password: ");
            password = input.nextLine();
            if (checkPasswordComplexity(password)) {
                break;
            }
            System.out.println("Password is incorrectly formatted. Please try again");
        }

        while (true) {
            System.out.print("Enter your cellphone number (+27): ");
            cellPhone = input.nextLine();
            if (checkCellPhoneNumber(cellPhone)) {
                break;
            }
            System.out.println("Cellphone number is incorrectly formatted. Please try again");
        }

        registeredUsername = username;
        registeredPassword = password;
        registeredCellPhone = cellPhone;
        System.out.println("User registered successfully.");
    }

    // Login: keeps asking until the details match the registered ones
    public static boolean loginUser(String enteredUsername, String enteredPassword) {
        while (true) {
            if (enteredUsername.equals(registeredUsername)
                    && enteredPassword.equals(registeredPassword)) {
                return true;
            }
            System.out.println("Username or password incorrect, please try again");
            System.out.print("Enter your username to login: ");
            enteredUsername = input.nextLine();
            System.out.print("Enter your password to login: ");
            enteredPassword = input.nextLine();
        }
    }

    public static String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome, it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // Reads a whole number, asks again if the user does not type a number
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // Asks for the recipient until the number is valid
    private static String askForRecipient() {
        while (true) {
            System.out.print("Enter the recipient cell number (with international code): ");
            String recipient = input.nextLine().trim();
            String result = Message.checkRecipientNumber(recipient);
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) {
                return recipient;
            }
        }
    }

    // Asks for the message text until it is 250 characters or less
    private static String askForMessageText() {
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            String text = input.nextLine();
            System.out.println(Message.checkMessageLength(text));
            if (Message.isMessageLengthValid(text)) {
                return text;
            }
        }
    }

    // Asks send / disregard / store until the user picks 1, 2 or 3
    private static int askForChoice() {
        while (true) {
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int choice = readInt("Choose an option: ");
            if (choice >= 1 && choice <= 3) {
                return choice;
            }
            System.out.println("Invalid option, please choose 1, 2 or 3.");
        }
    }

    // A for loop lets the user enter the number of messages they chose
    public static void sendMessages() {
        int numMessages;
        while (true) {
            numMessages = readInt("How many messages do you want to enter? ");
            if (numMessages > 0) {
                break;
            }
            System.out.println("Please enter a number greater than 0.");
        }

        // The loop counter i is also the message number (0, 1, 2 ...)
        for (int i = 0; i < numMessages; i++) {
            System.out.println("--- Message " + (i + 1) + " of " + numMessages + " ---");

            String recipient = askForRecipient();
            String text = askForMessageText();

            Message message = new Message(i, recipient, text);
            System.out.println("Message ID generated: " + message.getMessageID());

            int choice = askForChoice();
            System.out.println(message.sentMessage(choice));

            if (choice == 1) {
                System.out.println(message.getDetails());
            }
        }

        System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
    }

    public static void main(String[] args) {
        registerUser();

        System.out.print("Enter your username to login: ");
        String loginUsername = input.nextLine();
        System.out.print("Enter your password to login: ");
        String loginPassword = input.nextLine();

        boolean loginSuccessful = loginUser(loginUsername, loginPassword);
        System.out.println(returnLoginStatus(loginSuccessful));

        if (!loginSuccessful) {
            return;
        }

        System.out.println("Welcome to QuickChat.");

        // Menu runs until the user chooses Quit
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int option = readInt("Choose an option: ");

            if (option == 1) {
                sendMessages();
            } else if (option == 2) {
                System.out.println("Coming Soon.");
            } else if (option == 3) {
                System.out.println("Goodbye.");
                running = false;
            } else {
                System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }
    }
}
