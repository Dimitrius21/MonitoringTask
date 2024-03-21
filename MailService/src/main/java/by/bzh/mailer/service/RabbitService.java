package by.bzh.mailer.service;

import by.bzh.mailer.domain.RabbitMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitService {
    @Value("${mail.from.address}")
    private String emailFrom;
    @Value("${mail.subject}")
    private String emailSubject;
    private final MailService mailService;
    private final ObjectMapper mapper;

    @RabbitListener(queues = {"${rabbit.queue.email}"})
    public void noteHandler(String mes) {
        try {
            RabbitMessage gotMessage = mapper.readValue(mes, RabbitMessage.class);
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            List<String> emailList = gotMessage.getEmailList();
            String[] emails = emailList.toArray(new String[0]);
            emailMessage.setFrom(emailFrom);
            emailMessage.setTo(emails);
            emailMessage.setSubject(emailSubject);
            emailMessage.setText(gotMessage.getMessage());
            mailService.sendMail(emailMessage);
        } catch (JsonProcessingException e) {
            log.error("Can't parse got message: {}", mes);
        } catch (MailException e) {
            log.error("Can't send email due to: {}", e.getMessage());
        }

    }
}
