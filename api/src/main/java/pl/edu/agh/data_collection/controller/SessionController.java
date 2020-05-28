package pl.edu.agh.data_collection.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import pl.edu.agh.data_collection.utils.TimestampParser;
import pl.edu.agh.data_collection.utils.UsernameParser;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(ContextPath.SESSION_MAIN_PATH)
public class SessionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SessionRepository sessionRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TimestampParser timestampParser;
    private final UsernameParser bearerParser;

    private static final String FULL_PATH_ADD_SESSION_ELEMENT = ContextPath.SESSION_MAIN_PATH + ContextPath.SESSION_ADD_ELEMENT_PATH;

    @Autowired
    public SessionController(SessionRepository sessionRepository, EventRepository eventRepository, UserRepository userRepository, TimestampParser timestampParser, UsernameParser bearerParser) {
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.timestampParser = timestampParser;
        this.bearerParser = bearerParser;
    }

    @PostMapping(ContextPath.SESSION_ADD_ELEMENT_PATH)
    public ResponseEntity<Object> addSessionElement(@Valid @RequestBody SessionDto sessionDto, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) {
        List<SessionEntity> sessions = new LinkedList<>();

        Long userId = getUserIdFromHeader(header);

        for(SessionDto.SessionElement element : sessionDto.getSessions()){
            SessionEntity sessionEntity = buildSessionEntityOutOfDao(element, userId);
            sessions.add(sessionEntity);
        }

        sessionRepository.saveAll(sessions);
        logger.info("{}: {} ---> session element saved by user: {}", HttpMethod.POST, FULL_PATH_ADD_SESSION_ELEMENT, userId);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    private Long getUserIdFromHeader(String header) {
        String login = bearerParser.parse(header);
        Optional<Long> optionalId = userRepository.getIdByLogin(login);
        return optionalId.orElseThrow(() -> new UsernameNotFoundException(login + "not found!"));
    }

    private SessionEntity buildSessionEntityOutOfDao(SessionDto.SessionElement element, Long userId){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUserId(userId);
        sessionEntity.setX(element.getX_cor());
        sessionEntity.setY(element.getY_cor());

        EventEntity foundEvent = eventRepository.findByName(element.getEvent());

        sessionEntity.setEvent(foundEvent.getId());

        sessionEntity.setEventTime(timestampParser.parse(element.getTime()));

        sessionEntity.setXResolution(element.getX_res());
        sessionEntity.setYResolution(element.getY_res());

        return sessionEntity;
    }
}
