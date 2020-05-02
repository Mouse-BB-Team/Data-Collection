package pl.edu.agh.data_collection.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class TimestampParserTest {

    private static final TimestampParser parser = new TimestampParser();

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    @BeforeAll
    static void setUp(){
        parser.setTimestampFormat(TIMESTAMP_FORMAT);
    }

    @Test
    void parseTest() {
        String properTimestamp = "2012-03-13 16:02:35.322";

        LocalDateTime time = parser.parse(properTimestamp);

        assertEquals(2012, time.getYear());
        assertEquals(Month.MARCH, time.getMonth());
        assertEquals(13, time.getDayOfMonth());
        assertEquals(16, time.getHour());
        assertEquals(2, time.getMinute());
        assertEquals(35, time.getSecond());
        assertEquals(322, nanoToMilli(time.getNano()));
    }

    private int nanoToMilli(int value){
        return value / 1000_000;
    }

}