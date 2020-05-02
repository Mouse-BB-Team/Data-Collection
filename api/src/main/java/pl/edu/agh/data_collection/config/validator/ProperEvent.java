package pl.edu.agh.data_collection.config.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ProperEventValidator.class)
public @interface ProperEvent {
    String[] value() default {};

    String message() default "Event isn't proper";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
