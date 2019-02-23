package ru.tony.smtpbot.smpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.server.SMTPServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpServerService {

    private static final String SERVER_START_INFO = "****** SMTP Server is running for domain %s on port: %s";
    private static final String SERVER_STOP_INFO = "****** Stopping SMTP Server for domain %s on port %s";
    private SMTPServer smtpServer;

    private final MessageHandlerFactory handlerFactory;
    private final SmtpServerProperties properties;

    @PostConstruct
    public void start() {
        smtpServer = new SMTPServer(handlerFactory);

        smtpServer.setHostName(properties.getHostName());
        smtpServer.setPort(properties.getPort());

        smtpServer.start();

        log.info(format(SERVER_START_INFO, smtpServer.getHostName(), smtpServer.getPort()));
    }

    @PreDestroy
    public void stop() {
        if (isRunning()) {
            log.info(format(SERVER_STOP_INFO, smtpServer.getHostName(), smtpServer.getPort()));
            smtpServer.stop();
        }
    }

    private boolean isRunning() {
        return smtpServer.isRunning();
    }
}
