import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    void username_valid() {
        assertTrue(Main.checkUserName("kyl_1"));
    }

    @Test
    void username_invalid() {
        assertFalse(Main.checkUserName("kyle!!!!!!"));
        assertFalse(Main.checkUserName("kyle1"));
    }

    @Test
    void password_valid() {
        assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void password_invalid() {
        assertFalse(Main.checkPasswordComplexity("password"));
        assertFalse(Main.checkPasswordComplexity("Short1!"));
    }

    @Test
    void cellPhone_valid() {
        assertTrue(Main.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void cellPhone_invalid() {
        assertFalse(Main.checkCellPhoneNumber("08966553"));
    }

    @Test
    void loginStatus_messages() {
        assertEquals("Welcome, it is great to see you again.", Main.returnLoginStatus(true));
        assertEquals("Username or password incorrect, please try again.", Main.returnLoginStatus(false));
    }
}
