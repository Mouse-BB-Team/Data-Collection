package pl.edu.agh.data_collection.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@Component
public class TimestampParser {

    @Value("${default.time.format}")
    private String timestampFormat;

    public Calendar parse(String timestamp) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timestampFormat, Locale.ENGLISH);
        calendar.setTime(simpleDateFormat.parse(timestamp));
        return calendar;
    }

    void setTimestampFormat(String format){
        timestampFormat = format;
    }
}
