package ru.tony.smtpbot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.tony.smtpbot.bot.commands.BotCommand;
import ru.tony.smtpbot.bot.commands.BotCommandManager;

import java.util.Optional;

import static ru.tony.smtpbot.bot.Answer.answer;

@Component
@RequiredArgsConstructor
public class EmailBot extends TelegramLongPollingBot {

    private static final String ERROR_MESSAGE = "Available commands:\n" +
            "/register mail@example.com\n" +
            "/deregister mail@example.com";
    private static final String UNKNOWN_COMMAND = "Unknown command";

    private final BotCommandManager commandManager;
    private final BotProperties properties;


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().isCommand()) {
                onCommandAction(update);
            } else {
                onAnotherMessageAction(update);
            }
        }

    }

    private void onAnotherMessageAction(Update update) {
        answerBack(update, ERROR_MESSAGE);
    }

    private void answerBack(Update update, String errorMessage) {
        answer(this).toChat(update.getMessage().getChatId()).message(errorMessage).send();
    }

    private void onCommandAction(Update update) {
        Optional<BotCommand> command = commandManager.getCommand(update.getMessage().getText());
        command.ifPresentOrElse(botCommand -> botCommand.execute(this, update),
                () -> { answerBack(update, UNKNOWN_COMMAND);
        });
    }

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }
}
