package ru.tony.smtpbot.smpt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "smtp")
public class SmtpServerProperties {

    private int port;
    private String hostName;
}
