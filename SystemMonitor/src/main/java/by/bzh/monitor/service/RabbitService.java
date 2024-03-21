package by.bzh.monitor.service;

import by.bzh.monitor.domain.RabbitMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitService {
    @Value("${rabbit.routing-key.email}")
    private String routingKey;

    private final RabbitTemplate template;
    private final DirectExchange exchange;
    private final ObjectMapper mapper;

    public void send(RabbitMessage mes)  {
        try {
            String message = mapper.writeValueAsString(mes);
            template.convertAndSend(exchange.getName(), routingKey, message);
            log.info("Sent to Rabbit: {}", message);
        }catch (JsonProcessingException e){
            log.error("Error of convert the message to Json: {}, it hasn't been sent to RabbitMq", mes);
        }
    }
}
