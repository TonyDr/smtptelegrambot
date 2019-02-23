package ru.tony.smtpbot.notification;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tony.smtpbot.bot.Answer;
import ru.tony.smtpbot.bot.EmailBot;
import ru.tony.smtpbot.smpt.EmailData;

import java.util.Set;

import static java.lang.String.format;

@Component
@AllArgsConstructor
public class EmailNotifier {

    private static final String NOTIFY_MESSAGE = "From: %s\nTo: %s\nSubject: %s\nBody:\n%s";

    private final EmailBot bot;
    private final SubscriptionManager manager;

    public void notify(EmailData email) {
        Set<Long> subscribers = manager.findSubscribers(email.getTo());
        subscribers.forEach(chat -> {
            Answer.answer(bot).toChat(chat).message(getMessage(email)).send();
        });
    }

    private String getMessage(EmailData email) {
        return format(NOTIFY_MESSAGE, email.getFrom(),
                email.getTo(), email.getSubject(), email.getMessage());
    }
}
