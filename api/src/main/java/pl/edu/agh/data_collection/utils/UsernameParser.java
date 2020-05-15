package pl.edu.agh.data_collection.utils;

import com.google.gson.Gson;

import java.util.Map;

public class UsernameParser extends ParserDecorator<String, String>{

    private static final String PROPERTY_NAME = "user_name";
    private static final Gson mapper = new Gson();

    public UsernameParser(JwtDecoder wrappedParser) {
        super(wrappedParser);
    }

    public String parse(String objectToParse) {
        objectToParse = objectToParse.split(" ")[1];
        return (String) mapper.fromJson(wrappedParser.parse(objectToParse), Map.class).get(PROPERTY_NAME);
    }
}
