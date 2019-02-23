package ru.tony.smtpbot.smpt;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tony.smtpbot.bot.EmailBot;
import ru.tony.smtpbot.notification.EmailNotifier;
import ru.tony.smtpbot.notification.SubscriptionManager;

import static java.util.Set.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailNotifierTest {

    private EmailNotifier sut;
    private SubscriptionManager subscriptionManager;
    private EmailBot bot;

    @Captor
    private ArgumentCaptor<SendMessage> messageCaptor;

    @Before
    public void beforeMethod() {
        initMocks(this);
        subscriptionManager = mock(SubscriptionManager.class);
        bot = mock(EmailBot.class);
        sut = new EmailNotifier(bot, subscriptionManager);
    }

    @Test
    public void shouldCorrectlyNotify() throws TelegramApiException {
        when(subscriptionManager.findSubscribers("to@mail.com")).thenReturn(of(12L, 14L));
        sut.notify(EmailData.builder()
                .from("test@mail.com")
                .to("to@mail.com")
                .subject("MailSubject")
                .message("Test message")
                .build());


        verify(bot, times(2)).execute(messageCaptor.capture());
        assertEquals(2, messageCaptor.getAllValues().size());
    }

    @Test
    public void shouldNotSendNotificationWhenSubscribersDoNotExist() throws TelegramApiException {
        String to = "to@mail.com";
        when(subscriptionManager.findSubscribers(to)).thenReturn(of());
        sut.notify(EmailData.builder()
                .from("test@mail.com")
                .to(to)
                .subject("MailSubject")
                .message("Test message")
                .build());

        verify(subscriptionManager).findSubscribers(to);
        verify(bot, times(0)).execute(any(SendMessage.class));

    }
}