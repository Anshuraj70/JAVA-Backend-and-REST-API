
package com.javabackend.validation;
// import com.javabackend.exception.InvalidCommandException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
// import java.util.Arrays;
// import java.util.List;

public class CommandValidator implements ConstraintValidator<ValidCommand, String> {
    
    // private static final List<String> FORBIDDEN_COMMANDS = Arrays.asList(
    //     // "rm -rf", "sudo", "chmod", ">", ">>", "|", "&"
    // );
    private static final String[] DANGEROUS_PATTERNS = {
        "rm -rf",
        "dd if=/dev/zero",
        "mkfs",
        "fdisk",
        "> /dev/sd",
        "sudo",
        "chmod 777",
        "eval",
        "exec",
        "passwd",
        "useradd",
        "userdel",
        "kill -9",
        "systemctl",
        ":() { :|:&"  // Fork bomb
    };


    private static final String[] DANGEROUS_SYMBOLS = {
        "&&", "||", ";", "&", "|", "`", "$("
    };

    // @Override
    // public boolean isValid(String command, ConstraintValidatorContext context) {
    //     if (command == null || command.trim().isEmpty()) {
    //         return false;
    //     }

    //     // Check for forbidden commands/characters
    //     return FORBIDDEN_COMMANDS.stream()
    //         .noneMatch(forbidden -> command.toLowerCase().contains(forbidden));
    // }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;  // null is validated by @NotNull
        }

        String lowerValue = value.toLowerCase().trim();

        // Check for dangerous patterns
        for (String pattern : DANGEROUS_PATTERNS) {
            if (lowerValue.contains(pattern.toLowerCase())) {
                addConstraintViolation(context,
                    "Command contains dangerous pattern: " + pattern);
                // throw new InvalidCommandException("Command contains dangerous pattern: " + pattern);
                return false;
            }
        }

        // Check for dangerous symbol combinations
        for (String symbol : DANGEROUS_SYMBOLS) {
            if (lowerValue.contains(symbol)) {
                addConstraintViolation(context,
                    "Command contains potentially dangerous symbol: " + symbol);
                // throw new InvalidCommandException("Command contains potentially dangerous symbol: " + symbol);
                return false;
            }
        }

        // Check command length (prevent buffer overflow)
        if (value.length() > 1000) {
            addConstraintViolation(context,
                "Command is too long (max 1000 characters)");
            // throw new InvalidCommandException("Command is too long (max 1000 characters)");
            return false;
        }

        return true;
    }
    
    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }

}