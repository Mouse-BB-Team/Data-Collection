package pl.edu.agh.data_collection.config.validator;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class ProperTimestampFormatValidator implements ConstraintValidator<ProperTimestamp, String> {
    @Value("${default.time.format}")
    private String timestampFormat;
    private DateTimeFormatter formatter;

    @Override
    public void initialize(ProperTimestamp constraintAnnotation) {
        formatter = DateTimeFormatter.ofPattern(timestampFormat).withResolverStyle(ResolverStyle.SMART);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalDate.parse(value, formatter);
        } catch (DateTimeParseException ex){
            return false;
        }

        return true;
    }

    void setTimestampFormat(String format){
        timestampFormat = format;
    }
}
