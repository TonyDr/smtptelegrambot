package ru.tony.smtpbot.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tony.smtpbot.bot.commands.BotCommandManager;
import ru.tony.smtpbot.bot.commands.DeregisterCommand;
import ru.tony.smtpbot.bot.commands.RegisterCommand;
import ru.tony.smtpbot.bot.validator.EmailValidator;
import ru.tony.smtpbot.notification.SubscriptionManager;

import java.io.IOException;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailBotTest {

    private static final String BOT_NAME = "botName";
    private static final String BOT_TOKEN = "botToken";

    private EmailBot sut;

    private ObjectMapper mapper = new ObjectMapper();

    @Captor
    private ArgumentCaptor<SendMessage> messageCaptor;
    @Captor
    private ArgumentCaptor<Long> chatIdCaptor;
    @Captor
    private ArgumentCaptor<String> emailCaptor;

    private SubscriptionManager subscriptionManager;
    private BotProperties properties;

    @Before
    public void beforeTest() throws TelegramApiException {
        initMocks(this);
        subscriptionManager = mock(SubscriptionManager.class);
        EmailValidator emailValidator = new EmailValidator();
        initBotProperties();
        sut = spy(new EmailBot(new BotCommandManager(new RegisterCommand(subscriptionManager, emailValidator),
                new DeregisterCommand(subscriptionManager, emailValidator)), properties));
        doReturn(null).when(sut).execute(any(SendMessage.class));

    }

    @Test
    public void propertiesPassedCorrectly() {
        assertEquals(BOT_NAME, sut.getBotUsername());
        assertEquals(BOT_TOKEN, sut.getBotToken());
    }

    @Test
    public void shouldAnswerToNotCommandMessage() throws IOException, TelegramApiException {
        Update update = createUpdate("{\"message\": {\"chat\": {\"id\":1234}}}");

        sut.onUpdateReceived(update);

        verify(sut).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        assertEquals("1234", message.getChatId());
        assertEquals("Available commands:\n" +
                "/register mail@example.com\n" +
                "/deregister mail@example.com", message.getText());
    }

    @Test
    public void sendFailMessageWhenReceiveInvalidEmailToSubscribe() throws IOException, TelegramApiException {
        Update update = createUpdate("{\"message\":{" +
                "\"chat\": {\"id\":1234}," +
                "\"text\":\"/register mail@mail\"," +
                "\"entities\":[{\"type\":\"bot_command\", \"offset\":0 }]}}");

        sut.onUpdateReceived(update);

        verify(sut).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        assertEquals("1234", message.getChatId());
        assertEquals("Invalid email", message.getText());
    }

    @Test
    public void shouldCorrectlySubscribeToEmailNotification() throws IOException, TelegramApiException {
        Update update = createUpdate("{\"message\":{" +
                "\"chat\": {\"id\":1234}," +
                "\"text\":\"/register mail@mail.ru\"," +
                "\"entities\":[{\"type\":\"bot_command\", \"offset\":0 }]}}");

        long chatId = 1234L;
        when(subscriptionManager.findSubscriptions(chatId)).thenReturn(Set.of("mail@mail.ru", "mail@mail.com"));

        sut.onUpdateReceived(update);

        verify(sut).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        assertEquals("1234", message.getChatId());
        assertEquals("You subscribed to:\n" +
                "mail@mail.com\n" +
                "mail@mail.ru", message.getText());
        verify(subscriptionManager).subscribe(chatIdCaptor.capture(), emailCaptor.capture());
        assertEquals(chatId, chatIdCaptor.getValue().intValue());
        assertEquals("mail@mail.ru", emailCaptor.getValue());
    }


    @Test
    public void sendFailMessageWhenReceiveInvalidEmailToUnsubscribe() throws IOException, TelegramApiException {
        Update update = createUpdate("{\"message\":{" +
                "\"chat\": {\"id\":1234}," +
                "\"text\":\"/deregister mail@mail\"," +
                "\"entities\":[{\"type\":\"bot_command\", \"offset\":0 }]}}");

        sut.onUpdateReceived(update);

        verify(sut).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        assertEquals("1234", message.getChatId());
        assertEquals("Invalid email", message.getText());
    }

    @Test
    public void shouldCorrectlyUnsubscribeFromEmail() throws IOException, TelegramApiException {
        Update update = createUpdate("{\"message\":{" +
                "\"chat\": {\"id\":1234}," +
                "\"text\":\"/deregister mail@mail.ru\"," +
                "\"entities\":[{\"type\":\"bot_command\", \"offset\":0 }]}}");

        long chatId = 1234L;
        when(subscriptionManager.findSubscriptions(chatId)).thenReturn(singleton("mail@mail.com"));

        sut.onUpdateReceived(update);

        verify(sut).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        assertEquals("1234", message.getChatId());
        assertEquals("You also subscribed to:\n" +
                "mail@mail.com", message.getText());
        verify(subscriptionManager).unsubscribe(chatIdCaptor.capture(), emailCaptor.capture());
        assertEquals(chatId, chatIdCaptor.getValue().intValue());
        assertEquals("mail@mail.ru", emailCaptor.getValue());
    }

    @Test
    public void sendFailWithUnrecognizedCommand() throws IOException, TelegramApiException {
        Update update = createUpdate("{\"message\":{" +
                "\"chat\": {\"id\":1234}," +
                "\"text\":\"/deregiste mail@mail\"," +
                "\"entities\":[{\"type\":\"bot_command\", \"offset\":0 }]}}");

        sut.onUpdateReceived(update);

        verify(sut).execute(messageCaptor.capture());
        SendMessage message = messageCaptor.getValue();
        assertEquals("1234", message.getChatId());
        assertEquals("Unknown command", message.getText());
    }


    private Update createUpdate(String content) throws IOException {
        return mapper.readValue(content, Update.class);
    }

    private void initBotProperties() {
        properties = new BotProperties();
        properties.setName(BOT_NAME);
        properties.setToken(BOT_TOKEN);
    }
}
