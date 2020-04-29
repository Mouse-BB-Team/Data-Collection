package pl.edu.agh.data_collection.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.data_collection.config.ContextPath;
import pl.edu.agh.data_collection.dto.SessionDto;
import pl.edu.agh.data_collection.persistence.entity.EventEntity;
import pl.edu.agh.data_collection.persistence.entity.SessionEntity;
import pl.edu.agh.data_collection.persistence.repository.EventRepository;
import pl.edu.agh.data_collection.persistence.repository.SessionRepository;
import pl.edu.agh.data_collection.utils.TimestampParser;

import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping(ContextPath.SESSION_MAIN_PATH)
public class SessionModel {

    private final SessionRepository sessionRepository;
    private final EventRepository eventRepository;
    private final TimestampParser parser;

    @Autowired
    public SessionModel(SessionRepository sessionRepository, EventRepository eventRepository, TimestampParser parser) {
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
        this.parser = parser;
    }

    @PostMapping(ContextPath.SESSION_ADD_ELEMENT_PATH)
    public ResponseEntity<Object> addSessionElement(@RequestBody SessionDto sessionDto) throws ParseException {
        List<SessionEntity> sessions = new LinkedList<>();

        for(SessionDto.SessionElement element : sessionDto.getSessions()){
            SessionEntity sessionEntity = buildSessionEntityOutOfDao(element);
            sessions.add(sessionEntity);
        }

        sessionRepository.saveAll(sessions);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    private SessionEntity buildSessionEntityOutOfDao(SessionDto.SessionElement element) throws ParseException {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUserId(element.getUser_id());
        sessionEntity.setX(element.getX_cor());
        sessionEntity.setY(element.getY_cor());

        Optional<EventEntity> foundEventOptional = eventRepository.findByName(element.getEvent());

        sessionEntity.setEvent(foundEventOptional.orElse(new EventEntity()).getId());

        sessionEntity.setEventTime(parser.parse(element.getTime()));

        return sessionEntity;
    }
}
