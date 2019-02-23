package ru.tony.smtpbot.smpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import ru.tony.smtpbot.notification.EmailNotifier;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class EmailMessageHandler implements MessageHandler {

    private final EmailNotifier notifier;
    private final MessageContext context;
    private String from;
    private String to;
    private String message;
    private String subject;

    @Override
    public void from(String from) throws RejectException {
        this.from = from;
    }

    @Override
    public void recipient(String recipient) throws RejectException {
        this.to = recipient;
    }

    @Override
    public void data(InputStream data) throws RejectException, TooMuchDataException, IOException {
        readDataFromStream(data);
    }

    @Override
    public void done() {
        notifier.notify(EmailData.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .message(message).build());
    }

    private void readDataFromStream(InputStream is) {
        Session s = Session.getInstance(new Properties());
        try {
            MimeMessage msg = new MimeMessage(s, is);
            this.subject = msg.getSubject();
            this.message = (String) msg.getContent();
        } catch (MessagingException | IOException e) {
            log.error("Error when parse data", e);
        }
    }

}
