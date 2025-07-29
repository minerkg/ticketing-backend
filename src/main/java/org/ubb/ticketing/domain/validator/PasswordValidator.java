package org.ubb.ticketing.domain.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String password = (String) target;

        if (password == null || password.isBlank()) {
            errors.rejectValue(
                    "password",
                    "password.empty",
                    "Password must not be empty.");
            return;
        }

        if (password.length() < 8) {
            errors.rejectValue(
                    "password",
                    "password.tooShort",
                    "Password must be at least 8 characters long.");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.rejectValue(
                    "password",
                    "password.noUppercase",
                    "Password must contain at least one uppercase letter.");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.rejectValue(
                    "password",
                    "password.noLowercase",
                    "Password must contain at least one lowercase letter.");
        }

        if (!password.matches(".*\\d.*")) {
            errors.rejectValue(
                    "password",
                    "password.noDigit",
                    "Password must contain at least one digit.");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errors.rejectValue(
                    "password",
                    "password.noSpecialChar",
                    "Password must contain at least one special character.");
        }
    }

}
