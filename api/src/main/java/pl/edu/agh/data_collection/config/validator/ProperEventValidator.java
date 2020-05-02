package pl.edu.agh.data_collection.config.validator;

import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.agh.data_collection.persistence.repository.EventRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;


public class ProperEventValidator implements ConstraintValidator<ProperEvent, String> {

    private final EventRepository eventRepository;
    private List<String> events;

    @Autowired
    public ProperEventValidator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void initialize(ProperEvent constraintAnnotation) {
        events = eventRepository.getAllNames();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        synchronized (this) {
            return events.contains(value);
        }
    }
}
