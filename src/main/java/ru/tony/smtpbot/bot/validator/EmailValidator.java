package ru.tony.smtpbot.bot.validator;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    private static final String EMAIL_REGEXP = "^\\w+@\\w+\\..{2,3}(.{2,3})?$";

    public boolean isValid(String email) {
        return email.matches(EMAIL_REGEXP);
    }
}
