package pl.edu.agh.data_collection.utils;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class LoginParserTest {

    private final LoginParser parser = new LoginParser();

    @Test
    void parseProperlyTest() throws ParseException {
        byte[] parsedText = Base64.getEncoder().encode("admin:admin".getBytes());
        String parsedString = new String(parsedText);

        String parsedLogin = parser.parse(parsedString);
        assertEquals("admin", parsedLogin);
    }

    @Test
    void parseTextWithoutColonAndThrowsExceptionTest(){
        byte[] parsedText = Base64.getEncoder().encode("admin".getBytes());
        String parsedString = new String(parsedText);

        assertThrows(ParseException.class, () -> parser.parse(parsedString));
    }

}