package pl.edu.agh.data_collection.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtDecoderTest {

    private static final JwtDecoder decoder = new JwtDecoder();

    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1ODk1NDExODQsInVzZXJfbmFtZS" +
            "I6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiJaRS9qVGt4SktYNldoSVZ3SGF1U1ZKNFdSTU09IiwiY2xpZ" +
            "W50X2lkIjoiY2xpZW50X2lkIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl19.AVdnJF9ji3I0eURrov1vLroIn9WKhLtWXvAS85nzJvc";

    private static final String DECODED_TOKEN = "{\"exp\":1589541184,\"user_name\":\"admin\",\"authorities\":[\"ROLE_ADMIN\"]," +
            "\"jti\":\"ZE/jTkxJKX6WhIVwHauSVJ4WRMM=\",\"client_id\":\"client_id\",\"scope\":[\"read\",\"write\"]}";

    @Test
    void parseTest(){
        assertEquals(DECODED_TOKEN, decoder.parse(JWT_TOKEN));
    }
}