package pl.edu.agh.data_collection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.data_collection.config.ContextPath;
import pl.edu.agh.data_collection.config.ProfileType;
import pl.edu.agh.data_collection.dto.SessionDto;
import pl.edu.agh.data_collection.persistence.entity.EventEntity;
import pl.edu.agh.data_collection.persistence.entity.SessionEntity;
import pl.edu.agh.data_collection.persistence.repository.EventRepository;
import pl.edu.agh.data_collection.persistence.repository.SessionRepository;
import pl.edu.agh.data_collection.persistence.repository.UserRepository;
import pl.edu.agh.data_collection.utils.AuthorizationHeaderValueParser;
import pl.edu.agh.data_collection.utils.LoginParser;
import pl.edu.agh.data_collection.utils.TimestampParser;

import java.text.ParseException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SessionController.class)
@ActiveProfiles(value = ProfileType.TEST_PROFILE)
class SessionControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SessionRepository sessionRepository;
    @MockBean
    private EventRepository eventRepository;
    @SpyBean
    private AuthorizationHeaderValueParser authorizationHeaderParser;
    @SpyBean
    private LoginParser loginParser;
    @SpyBean
    private TimestampParser timestampParser;
    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<List<SessionEntity>> sessionCaptor;

    private static final String TIMESTAMP = "2012-03-13 16:02:35.322";
    private static final String EVENT_MOVE = "MOVE";
    private static final String EVENT_CLICK = "CLICK";
    private static final String USER_LOGIN = "admin";
    private static final String USER_PASSWORD = "admin";
    private static final Long USER_ID = 10L;
    private static String BASIC_AUTH_HEADER;

    private static SessionDto sessionDto;
    private static List<SessionDto.SessionElement> elements;

    @BeforeAll
    static void setUp(){
        BASIC_AUTH_HEADER = "Basic " + new String(Base64.getEncoder().encode((USER_LOGIN + ":" + USER_PASSWORD).getBytes()));

        sessionDto = new SessionDto(new LinkedList<>());
        elements = sessionDto.getSessions();
        elements.add(new SessionDto.SessionElement(2,3, EVENT_CLICK, TIMESTAMP));
        elements.add(new SessionDto.SessionElement(12, 3242, EVENT_MOVE, TIMESTAMP));
    }

    @Test
    void savesAllSessionsElementsProperlyTest() throws Exception {
        EventEntity eventClickEntity = new EventEntity(1L, EVENT_CLICK);
        EventEntity eventMoveEntity = new EventEntity(2L, EVENT_MOVE);

        byte[] content = MAPPER.writeValueAsBytes(sessionDto);

        List<SessionEntity> expectedSession = new LinkedList<>();
        expectedSession.add(buildSessionEntityOutOfDao(elements.get(0), eventClickEntity));
        expectedSession.add(buildSessionEntityOutOfDao(elements.get(1), eventMoveEntity));

        when(userRepository.getIdByLogin(anyString())).thenReturn(Optional.of(USER_ID));
        when(eventRepository.findByName(EVENT_CLICK)).thenReturn(Optional.of(new EventEntity(1L, EVENT_CLICK)));
        when(eventRepository.findByName(EVENT_MOVE)).thenReturn(Optional.of(new EventEntity(2L, EVENT_MOVE)));

        mockMvc.perform(post(ContextPath.SESSION_MAIN_PATH + ContextPath.SESSION_ADD_ELEMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
                .andExpect(status().isOk());

        verify(sessionRepository).saveAll(sessionCaptor.capture());

        assertEquals(expectedSession, sessionCaptor.getValue());
    }

    @Test
    void savesAllSessionsElementsWithNullEventTest() throws Exception {
        EventEntity eventClickEntity = new EventEntity(1L, EVENT_CLICK);

        byte[] content = MAPPER.writeValueAsBytes(sessionDto);

        List<SessionEntity> expectedSession = new LinkedList<>();
        expectedSession.add(buildSessionEntityOutOfDao(elements.get(0), eventClickEntity));
        expectedSession.add(buildSessionEntityOutOfDao(elements.get(1), new EventEntity()));

        when(userRepository.getIdByLogin(anyString())).thenReturn(Optional.of(USER_ID));
        when(eventRepository.findByName(EVENT_CLICK)).thenReturn(Optional.of(new EventEntity(1L, EVENT_CLICK)));
        when(eventRepository.findByName(EVENT_MOVE)).thenReturn(Optional.empty());

        mockMvc.perform(post(ContextPath.SESSION_MAIN_PATH + ContextPath.SESSION_ADD_ELEMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
                .andExpect(status().isOk());

        verify(sessionRepository).saveAll(sessionCaptor.capture());

        assertEquals(expectedSession, sessionCaptor.getValue());
    }

    private SessionEntity buildSessionEntityOutOfDao(SessionDto.SessionElement element, EventEntity eventEntity) {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUserId(USER_ID);
        sessionEntity.setX(element.getX_cor());
        sessionEntity.setY(element.getY_cor());

        sessionEntity.setEvent(eventEntity.getId());

        sessionEntity.setEventTime(timestampParser.parse(element.getTime()));

        return sessionEntity;
    }
}