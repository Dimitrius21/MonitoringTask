package by.bzh.monitor.domain;

import lombok.Data;

import java.util.List;

@Data
public class RabbitMessage {
    private String message;
    private List<String> emailList;
}
