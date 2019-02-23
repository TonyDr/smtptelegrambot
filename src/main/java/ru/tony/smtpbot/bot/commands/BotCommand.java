package ru.tony.smtpbot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface BotCommand {

    String commandName();

    void execute(AbsSender sender, Update update);
}
