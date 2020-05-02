package pl.edu.agh.data_collection.config.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ProperTimestampFormatValidator.class)
public @interface ProperTimestamp {
    String[] value() default {};

    String message() default "Timestamp isn't proper";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
