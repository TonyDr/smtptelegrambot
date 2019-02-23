package ru.tony.smtpbot.smpt;

import com.sun.mail.smtp.SMTPTransport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.subethamail.smtp.MessageHandlerFactory;
import ru.tony.smtpbot.notification.EmailNotifier;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SmtpServerServiceTest {

    private SmtpServerService sut;
    private EmailNotifier notifier;

    @Captor
    private ArgumentCaptor<EmailData> emailData;
    private SmtpServerProperties properties;

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        notifier = mock(EmailNotifier.class);
        MessageHandlerFactory factory = new EmailMessageHandlerFactory(notifier);
        properties = new SmtpServerProperties();
        properties.setHostName("localhost");
        properties.setPort(45650);
        sut = new SmtpServerService(factory, properties);
        sut.start();
    }

    @After
    public void afterTest() {
        sut.stop();
    }

    @Test
    public void test() throws MessagingException {
        String from = "mail@mail.com";
        String to = "tomail@mail.com";
        String subject = "Test subject";
        String text = "Test text";
        sendMessage(from, to, subject, text);

        verify(notifier).notify(emailData.capture());
        EmailData data = emailData.getValue();
        assertEquals(from, data.getFrom());
        assertEquals(to, data.getTo());
        assertEquals(subject, data.getSubject());
        assertEquals(text + System.lineSeparator(), data.getMessage());
    }

    private void sendMessage(String from, String to, String subject, String text) throws MessagingException {
        Session session = getSession();
        Message msg = createMessage(from, to, subject, text, session);
        SMTPTransport t = (SMTPTransport)session.getTransport("smtp");
        t.connect(properties.getHostName(), properties.getPort(), "test", "");
        t.sendMessage(msg, msg.getAllRecipients());
        System.out.println("Response: " + t.getLastServerResponse());
        t.close();
    }

    private Message createMessage(String from, String to, String subject, String text, Session session) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject);
        msg.setText(text);
        msg.setSentDate(new Date());
        return msg;
    }

    private Session getSession() {
        Properties props = System.getProperties();
        props.put("mail.smtps.host",String.format("%s:%s", properties.getHostName(), properties.getPort()));
        props.put("mail.smtps.auth","false");
        return Session.getDefaultInstance(props);
    }
}
