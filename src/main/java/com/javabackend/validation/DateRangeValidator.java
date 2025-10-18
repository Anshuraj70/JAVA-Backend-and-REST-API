// File: com/javabackend/validation/DateRangeValidator.java
package com.javabackend.validation;

import com.javabackend.model.TaskExecution;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, TaskExecution> {
    
    @Override
    public boolean isValid(TaskExecution value, ConstraintValidatorContext context) {
        if (value == null || value.getStartTime() == null || value.getEndTime() == null) {
            return true;  // null validation is handled by @NotNull
        }
        
    boolean isValid = value.getStartTime().isBefore(value.getEndTime());
        
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Start time must be before end time")
                   .addConstraintViolation();
        }
        
        return isValid;
    }
}