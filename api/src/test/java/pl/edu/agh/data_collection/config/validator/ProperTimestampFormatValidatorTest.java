package pl.edu.agh.data_collection.config.validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProperTimestampFormatValidatorTest {

    private static final ProperTimestampFormatValidator validator = new ProperTimestampFormatValidator();

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String PROPER_DATE = "2012-03-13 12:23:32.123";
    private static final String WRONG_DATE = "2013-02-33 12:23:32.123";
    private static final String INVALID_FORMAT_DATE = "2012 Mar 12 12:23:32.123";

    @BeforeAll
    static void initializeValidator(){
        validator.setTimestampFormat(TIMESTAMP_FORMAT);
        validator.initialize(null);
    }

    @Test
    void properDateValidateTest(){
        assertTrue(validator.isValid(PROPER_DATE, null));
    }

    @Test
    void wrongDateValidateTest(){
        assertFalse(validator.isValid(WRONG_DATE, null));
    }

    @Test
    void invalidFormatDateValidateTest(){
        assertFalse(validator.isValid(INVALID_FORMAT_DATE, null));
    }
}