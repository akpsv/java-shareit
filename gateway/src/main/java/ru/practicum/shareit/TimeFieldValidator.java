package ru.practicum.shareit;

import org.apache.commons.beanutils.PropertyUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

public class TimeFieldValidator implements ConstraintValidator<TimeFieldMatcher, Object> {
    private String fieldNameOfStartTIme;
    private String fieldNameOfEndTime;

    @Override
    public void initialize(TimeFieldMatcher constraintAnnotation) {
        fieldNameOfStartTIme = constraintAnnotation.fieldNameOfStartTime();
        fieldNameOfEndTime = constraintAnnotation.fieldNameOfEndTime();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            LocalDateTime startTime = (LocalDateTime) PropertyUtils.getProperty(value, fieldNameOfStartTIme);
            LocalDateTime endTime = (LocalDateTime) PropertyUtils.getProperty(value, fieldNameOfEndTime);
            return startTime.isBefore(endTime);
        } catch (Exception exception) {
            throw new ConstraintViolationException(exception.getMessage(), null);
        }
    }
}
