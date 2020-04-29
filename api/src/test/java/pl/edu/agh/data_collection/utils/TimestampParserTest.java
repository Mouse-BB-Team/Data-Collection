package pl.edu.agh.data_collection.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class TimestampParserTest {

    private static final TimestampParser parser = new TimestampParser();

    private static final String TIMESTAMP_FORMAT = "yyyy MMM dd HH:mm:ss:SSSS";

    @BeforeAll
    static void setUp(){
        parser.setTimestampFormat(TIMESTAMP_FORMAT);
    }

    @Test
    void parseTest() throws ParseException {
        String properTimestamp = "2012 Mar 13 16:02:35:322";

        Calendar calendar = parser.parse(properTimestamp);

        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(3 - 1, calendar.get(Calendar.MONTH));
        assertEquals(13, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(4, calendar.get(Calendar.HOUR));
        assertEquals(1, calendar.get(Calendar.AM_PM));
        assertEquals(2, calendar.get(Calendar.MINUTE));
        assertEquals(35, calendar.get(Calendar.SECOND));
        assertEquals(322, calendar.get(Calendar.MILLISECOND));
    }

    @Test
    void parseThrowExceptionTest(){
        String wrongTimestamp = "Mar 13 16:02:35:322";

        assertThrows(ParseException.class, () -> parser.parse(wrongTimestamp));
    }

}