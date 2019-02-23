package ru.tony.smtpbot.bot.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tony.smtpbot.bot.validator.EmailValidator;
import ru.tony.smtpbot.notification.SubscriptionManager;

import static java.lang.String.format;
import static java.lang.String.join;
import static ru.tony.smtpbot.bot.Answer.answer;

@Component
@RequiredArgsConstructor
public class DeregisterCommand implements BotCommand {

    private static final String SUBSCRIBED_TO_MESSAGE = "You also subscribed to:\n%s";
    private static final String DEREGISTER = "/deregister";
    private static final String INVALID_EMAIL = "Invalid email";
    private final SubscriptionManager subscriptionManager;
    private final EmailValidator emailValidator;

    @Override
    public String commandName() {
        return DEREGISTER;
    }

    @Override
    public void execute(AbsSender sender, Update update) {
        String email = substringEmail(update.getMessage().getText());
        if (emailValidator.isValid(email)) {
            Long chatId = getChatId(update);
            subscriptionManager.unsubscribe(chatId, email);
            answer(sender).toChat(chatId).message(getSubscriptionMessage(chatId)).send();
        } else {
            answer(sender).toChat(getChatId(update)).message(INVALID_EMAIL).send();
        }
    }

    private Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    private String getSubscriptionMessage(Long chatId) {
        return format(SUBSCRIBED_TO_MESSAGE, join("\n", subscriptionManager.findSubscriptions(chatId)));
    }

    private String substringEmail(String messageText) {
        return messageText.replace(commandName()+ " ", "");
    }
}
