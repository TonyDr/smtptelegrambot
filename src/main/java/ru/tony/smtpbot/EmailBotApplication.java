package ru.tony.smtpbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@Slf4j
@SpringBootApplication
public class EmailBotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(EmailBotApplication.class, args);
    }
}
