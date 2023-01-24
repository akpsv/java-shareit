package ru.practicum.shareit;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeFieldValidator.class)
public @interface TimeFieldMatcher {
    String message() default "{constraints.timeFieldMatcher}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fieldNameOfStartTime();

    String fieldNameOfEndTime();
}
