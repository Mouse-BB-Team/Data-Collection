package pl.edu.agh.data_collection.utils;

import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class AuthorizationHeaderValueParser implements Parser<String, String>{
    @Override
    public String parse(String arg) throws ParseException {
        int index = arg.indexOf(' ');

        if(index < 0)
            throw new ParseException("space sign has not been found in string", index);

        return arg.substring(index + 1);
    }
}
