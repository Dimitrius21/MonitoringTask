package by.bzh.monitor.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Value("${rabbit.queue.email}")
    private String queueName;
    @Value("${rabbit.routing-key.email}")
    private String routingKey;
    @Value("${rabbit.exchange.email}")
    private String exchangeName;
    @Bean
    public DirectExchange direct() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(queueName);
    }

    @Bean
    public Binding binding1a(DirectExchange direct,
                             Queue emailQueue) {
        return BindingBuilder.bind(emailQueue)
                .to(direct)
                .with(routingKey);
    }
}
