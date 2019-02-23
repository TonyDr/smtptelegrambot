package ru.tony.smtpbot.bot.commands;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Component
public class BotCommandManager {

    private final Map<String, BotCommand> botCommandMap;

    public BotCommandManager(BotCommand ...commands) {
        botCommandMap = Arrays.stream(commands)
                .collect(toMap(BotCommand::commandName, botCommand -> botCommand));
    }

    public Optional<BotCommand> getCommand(String message) {
        return Optional.ofNullable(botCommandMap.get(getCommandName(message)));
    }

    private String getCommandName(String message) {
        return message.split(" ")[0];
    }
}
