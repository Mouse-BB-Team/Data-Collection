package pl.edu.agh.data_collection.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameParserTest {

    private static final UsernameParser parser = new UsernameParser(new JwtDecoder());

    private static final String BEARER_AUTH_HEADER = "Bearer " +
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1ODk1NDExODQsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZX" +
            "MiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiJaRS9qVGt4SktYNldoSVZ3SGF1U1ZKNFdSTU09IiwiY2xpZW50X2lkIjoiY2xpZW50X2lkI" +
            "iwic2NvcGUiOlsicmVhZCIsIndyaXRlIl19.AVdnJF9ji3I0eURrov1vLroIn9WKhLtWXvAS85nzJvc";

    private static final String DECODED_USERNAME = "admin";

    @Test
    void parseTest(){
        assertEquals(DECODED_USERNAME, parser.parse(BEARER_AUTH_HEADER));
    }
}