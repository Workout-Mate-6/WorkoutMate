package com.example.workoutmate.domain.user.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context){
        if (password == null) {
            return true; // null이면 검증 통과
        }

        return password.matches(PASSWORD_PATTERN);
    }
}
