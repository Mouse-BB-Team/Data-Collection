package pl.edu.agh.data_collection.utils;

import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampParser implements Parser<String, LocalDateTime>{

    @Value("${default.time.format}")
    private String timestampFormat;

    @Override
    public LocalDateTime parse(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampFormat);
        return LocalDateTime.parse(timestamp, formatter);
    }

    void setTimestampFormat(String format){
        timestampFormat = format;
    }
}
