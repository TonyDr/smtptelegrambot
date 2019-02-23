package ru.tony.smtpbot.smpt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import ru.tony.smtpbot.notification.EmailNotifier;

@Component
@RequiredArgsConstructor
public class EmailMessageHandlerFactory implements MessageHandlerFactory {

    private final EmailNotifier notifier;

    @Override
    public MessageHandler create(MessageContext ctx) {
        return new EmailMessageHandler(notifier, ctx);
    }
}
