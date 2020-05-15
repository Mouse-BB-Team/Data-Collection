package pl.edu.agh.data_collection.utils;

import org.springframework.security.jwt.JwtHelper;

public class JwtDecoder implements Parser<String, String>{
    @Override
    public String parse(String objectToParse) {
        return JwtHelper.decode(objectToParse).getClaims();
    }
}
