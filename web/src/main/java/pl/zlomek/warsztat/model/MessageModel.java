package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class MessageModel {
    private String message;
    private String subject;

    public MessageModel(String message, String subject) {
        this.message = message;
        this.subject = subject;
    }
}
