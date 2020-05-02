package pl.edu.agh.data_collection.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimestampParser{

    @Value("${default.time.format}")
    private String timestampFormat;

    public LocalDateTime parse(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampFormat);
        return LocalDateTime.parse(timestamp, formatter);
    }

    void setTimestampFormat(String format){
        timestampFormat = format;
    }
}
