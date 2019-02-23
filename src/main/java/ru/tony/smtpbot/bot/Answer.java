package ru.tony.smtpbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class Answer {

    private AbsSender sender;
    private String message;
    private Long chatId;

    private Answer(AbsSender sender) {
        this.sender = sender;
    }

    public static Answer answer(AbsSender sender) {
        return new Answer(sender);
    }

    public Answer message(String message) {
        this.message = message;
        return this;
    }

    public Answer toChat(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public void send() {
        SendMessage msg = new SendMessage().setChatId(chatId).setText(message);
        try {
            sender.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Can't send message", e);
        }

    }
}
