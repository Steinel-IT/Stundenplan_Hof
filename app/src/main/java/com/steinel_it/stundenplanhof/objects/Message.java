package com.steinel_it.stundenplanhof.objects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {

    private final String message;
    private final String time;

    public Message(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getSendTime() {
        return time;
    }

    public static String getTimeAsString(LocalDateTime sendTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
        return formatter.format(sendTime);
    }
}
