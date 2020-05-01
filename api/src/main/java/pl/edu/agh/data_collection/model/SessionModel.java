package pl.edu.agh.data_collection.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.data_collection.config.ContextPath;
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
import java.util.*;

@RestController
@RequestMapping(ContextPath.SESSION_MAIN_PATH)
public class SessionModel {

    private final SessionRepository sessionRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TimestampParser timestampParser;
    private final LoginParser loginParser;
    private final AuthorizationHeaderValueParser authorizationHeaderParser;

    @Autowired
    public SessionModel(SessionRepository sessionRepository, EventRepository eventRepository, UserRepository userRepository, TimestampParser timestampParser, LoginParser loginParser, AuthorizationHeaderValueParser authorizationHeaderParser) {
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.timestampParser = timestampParser;
        this.loginParser = loginParser;
        this.authorizationHeaderParser = authorizationHeaderParser;
    }

    @PostMapping(ContextPath.SESSION_ADD_ELEMENT_PATH)
    public ResponseEntity<Object> addSessionElement(@RequestBody SessionDto sessionDto, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws ParseException {
        List<SessionEntity> sessions = new LinkedList<>();

        Long userId = getUserIdFromHeader(header);

        for(SessionDto.SessionElement element : sessionDto.getSessions()){
            SessionEntity sessionEntity = buildSessionEntityOutOfDao(element, userId);
            sessions.add(sessionEntity);
        }

        sessionRepository.saveAll(sessions);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    private Long getUserIdFromHeader(String header) throws ParseException {
        String headerValue = authorizationHeaderParser.parse(header);
        String login = loginParser.parse(headerValue);
        Optional<Long> optionalId = userRepository.getIdByLogin(login);
        return optionalId.orElseThrow(() -> new UsernameNotFoundException(login + "not found!"));
    }

    private SessionEntity buildSessionEntityOutOfDao(SessionDto.SessionElement element, Long userId) throws ParseException {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUserId(userId);
        sessionEntity.setX(element.getX_cor());
        sessionEntity.setY(element.getY_cor());

        Optional<EventEntity> foundEventOptional = eventRepository.findByName(element.getEvent());

        sessionEntity.setEvent(foundEventOptional.orElse(new EventEntity()).getId());

        sessionEntity.setEventTime(timestampParser.parse(element.getTime()));

        return sessionEntity;
    }
}