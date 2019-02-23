package ru.tony.smtpbot.bot.commands;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tony.smtpbot.bot.validator.EmailValidator;
import ru.tony.smtpbot.notification.SubscriptionManager;

import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.tony.smtpbot.bot.Answer.answer;

@Component
@RequiredArgsConstructor
public class RegisterCommand implements BotCommand {

    private static final String REGISTER = "/register";
    private static final String YOU_SUBSCRIBED_TO_MESSAGE = "You subscribed to:\n%s";
    private final SubscriptionManager subscriptionManager;
    private final EmailValidator emailValidator;

    @Override
    public String commandName() {
        return REGISTER;
    }

    @Override
    public void execute(AbsSender sender, Update update) {
        String email = getEmail(update);
        if (emailValidator.isValid(email)) {
            Long chatID = getChatId(update);
            subscriptionManager.subscribe(chatID, email);
            answer(sender).toChat(chatID).message(getSubscriptionsMessage(chatID)).send();
        } else {
            answer(sender).toChat(getChatId(update)).message("Invalid email").send();
        }

    }

    private String getSubscriptionsMessage(Long chatID) {
        return format(YOU_SUBSCRIBED_TO_MESSAGE,
                subscriptionManager
                        .findSubscriptions(chatID)
                        .stream()
                        .sorted()
                        .collect(Collectors.joining("\n")));
    }

    private Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    private String getEmail(Update update) {
        String messageText = update.getMessage().getText();
        return messageText.replace(commandName() + " ", "");
    }

}
