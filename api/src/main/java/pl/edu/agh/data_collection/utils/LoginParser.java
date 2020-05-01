package pl.edu.agh.data_collection.utils;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Base64;

@Component
public class LoginParser implements Parser<String, String>{
    public String parse(String header) throws ParseException{
        byte[] decodedBytes = Base64.getDecoder().decode(header);
        String decodedString = new String(decodedBytes);
        int colonIndex = decodedString.indexOf(':');

        if(colonIndex < 0)
            throw new ParseException("colon has not been found in string", colonIndex);

        return decodedString.substring(0, colonIndex);
    }
}
