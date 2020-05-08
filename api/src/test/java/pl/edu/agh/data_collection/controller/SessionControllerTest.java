package pl.edu.agh.data_collection.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.*;

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
    private static final String WRONG_EVENT = "wrong";
    private static final String USER_LOGIN = "admin";
    private static final String USER_PASSWORD = "admin";
    private static final Long USER_ID = 10L;
    private static String BASIC_AUTH_HEADER;
    private static String CONTEXT_PATH;

    @BeforeAll
    static void setUp(){
        CONTEXT_PATH = ContextPath.SESSION_MAIN_PATH + ContextPath.SESSION_ADD_ELEMENT_PATH;
        BASIC_AUTH_HEADER = "Basic " + new String(Base64.getEncoder().encode((USER_LOGIN + ":" + USER_PASSWORD).getBytes()));
    }

    @BeforeEach
    void setMockWhen(){
        when(eventRepository.getAllNames()).thenReturn(Arrays.asList(EVENT_CLICK, EVENT_MOVE));
        when(userRepository.getIdByLogin(anyString())).thenReturn(Optional.of(USER_ID));
        when(eventRepository.findByName(EVENT_CLICK)).thenReturn(new EventEntity(1L, EVENT_CLICK));
        when(eventRepository.findByName(EVENT_MOVE)).thenReturn(new EventEntity(2L, EVENT_MOVE));
    }

    @Test
    void validateMinXCoordinateTest() throws Exception {
        SessionDtoBuilder builder = new WrongXCoordinateSessionDtoBuilder();
        SessionDto sessionDto = builder.buildAndGet();

        byte[] content = MAPPER.writeValueAsBytes(sessionDto);

        mockMvc.perform(post(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateMinYCoordinateTest() throws Exception{
        SessionDtoBuilder builder = new WrongYCoordinateSessionDtoBuilder();
        SessionDto sessionDto = builder.buildAndGet();

        byte[] content = MAPPER.writeValueAsBytes(sessionDto);

        mockMvc.perform(post(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateMinXResolutionTest() throws Exception {
        SessionDtoBuilder builder = new WrongXResolutionSessionDtoBuilder();
        SessionDto sessionDto = builder.buildAndGet();

        byte[] content = MAPPER.writeValueAsBytes(sessionDto);

        mockMvc.perform(post(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateMinYResolutionTest() throws Exception {
        SessionDtoBuilder builder = new WrongYResolutionSessionDtoBuilder();
        SessionDto sessionDto = builder.buildAndGet();

        byte[] content = MAPPER.writeValueAsBytes(sessionDto);

        mockMvc.perform(post(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateProperEventTest() throws Exception{
        SessionDtoBuilder builder = new WrongEventSessionDtoBuilder();
        SessionDto sessionDto = builder.buildAndGet();

        byte[] content = MAPPER.writeValueAsBytes(sessionDto);

        mockMvc.perform(post(CONTEXT_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(content)
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }

    @Test
    void savesAllSessionsElementsProperlyTest() throws Exception {
        EventEntity expectedClickEvent = new EventEntity(1L, EVENT_CLICK);
        EventEntity expectedMoveEvent = new EventEntity(2L, EVENT_MOVE);

        SessionDtoBuilder builder = new ProperSessionDtoBuilder();
        SessionDto sessionDto = builder.buildAndGet();

        byte[] content = MAPPER.writeValueAsBytes(sessionDto);

        List<SessionEntity> expectedSession = new LinkedList<>();
        expectedSession.add(buildSessionEntityOutOfDao(sessionDto.getSessions().get(0), expectedClickEvent));
        expectedSession.add(buildSessionEntityOutOfDao(sessionDto.getSessions().get(1), expectedMoveEvent));

        mockMvc.perform(post(CONTEXT_PATH)
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

        sessionEntity.setXResolution(element.getX_res());
        sessionEntity.setYResolution(element.getY_res());


        return sessionEntity;
    }

    private abstract static class SessionDtoBuilder{

        protected List<SessionDto.SessionElement> elements;

        public SessionDto buildAndGet(){
            SessionDto sessionDto = new SessionDto(new LinkedList<>());
            elements = sessionDto.getSessions();
            addElement();
            return sessionDto;
        }

        protected abstract void addElement();
    }

    private static class ProperSessionDtoBuilder extends SessionDtoBuilder{

        @Override
        protected void addElement() {
            elements.add(new SessionDto.SessionElement(2,3, EVENT_CLICK, TIMESTAMP,12,1313));
            elements.add(new SessionDto.SessionElement(12, 3242, EVENT_MOVE, TIMESTAMP, 3212,2323));
        }
    }

    private static class WrongXCoordinateSessionDtoBuilder extends SessionDtoBuilder{

        @Override
        protected void addElement() {
            elements.add(new SessionDto.SessionElement(-5, 3, EVENT_CLICK, TIMESTAMP,3213,321));
        }
    }

    private static class WrongYCoordinateSessionDtoBuilder extends SessionDtoBuilder{

        @Override
        protected void addElement() {
            elements.add(new SessionDto.SessionElement(5, -3, EVENT_CLICK, TIMESTAMP,312,321));
        }
    }

    private static class WrongEventSessionDtoBuilder extends SessionDtoBuilder{

        @Override
        protected void addElement() {
            elements.add(new SessionDto.SessionElement(5, 3, WRONG_EVENT, TIMESTAMP,222,332));
        }
    }

    private static class WrongXResolutionSessionDtoBuilder extends SessionDtoBuilder{

        @Override
        protected void addElement() {
            elements.add(new SessionDto.SessionElement(5, 3, EVENT_CLICK, TIMESTAMP, -2, 0));
        }
    }

    private static class WrongYResolutionSessionDtoBuilder extends SessionDtoBuilder{

        @Override
        protected void addElement() {
            elements.add(new SessionDto.SessionElement(5, 3, EVENT_CLICK, TIMESTAMP, 0, -3));
        }
    }
}