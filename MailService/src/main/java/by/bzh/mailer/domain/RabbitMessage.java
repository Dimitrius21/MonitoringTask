package by.bzh.mailer.domain;

import lombok.Data;

import java.util.List;

@Data
public class RabbitMessage {
    private String message;
    private List<String> emailList;
}
