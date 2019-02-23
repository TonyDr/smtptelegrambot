package ru.tony.smtpbot.smpt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailData {

    private String from;
    private String to;
    private String message;
    private String subject;
}
