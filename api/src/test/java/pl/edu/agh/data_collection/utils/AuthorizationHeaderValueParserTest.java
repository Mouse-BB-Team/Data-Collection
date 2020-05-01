package pl.edu.agh.data_collection.utils;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationHeaderValueParserTest {

    private final AuthorizationHeaderValueParser parser = new AuthorizationHeaderValueParser();

    @Test
    void parseHeaderProperlyTest() throws ParseException {
        String header = "Basic value";
        String headerValue = parser.parse(header);
        assertEquals("value", headerValue);
    }

    @Test
    void parseHeaderWithoutSpaceSignThrowsExceptionTest(){
        String header = "Basic";
        assertThrows(ParseException.class, () -> parser.parse(header));
    }

}