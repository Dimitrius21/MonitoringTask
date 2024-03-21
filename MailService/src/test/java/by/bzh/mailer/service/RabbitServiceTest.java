package by.bzh.mailer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitServiceTest {
    @Mock
    MailService mailService;
    @Spy
    ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    RabbitService service;

    @Test
    void noteHandlerTest() {
        String message = """
                {"message" : "message",
                "emailList" : ["email1@mail.com", "email2@mail.com"] }
                """;
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom(null);
        emailMessage.setTo(new String[]{"email1@mail.com", "email2@mail.com"});
        emailMessage.setSubject(null);
        emailMessage.setText("message");

        service.noteHandler(message);
        verify(mailService).sendMail(emailMessage);
    }
}