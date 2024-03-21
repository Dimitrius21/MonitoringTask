package by.bzh.monitor.service;

import by.bzh.monitor.domain.entity.Email;
import by.bzh.monitor.exception.ResourceNotFountException;
import by.bzh.monitor.repository.EmailRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    EmailRepository repo;

    @InjectMocks
    EmailService service;

    @Test
    void deleteEmail() {
        String emailAddress = "email@mail.com";
        service.deleteEmail(emailAddress);

        verify(repo).deleteByEmail(emailAddress);
    }

    @Test
    void createEmail() {
        long id = 1;
        String emailAddress = "email@mail.com";
        Email emailForSave = getEmail(0, emailAddress);
        Email email = getEmail(id, emailAddress);
        when(repo.save(emailForSave)).thenReturn(email);

        Email res = service.createEmail(emailAddress);

        Assertions.assertThat(res).isEqualTo(email);
    }

    @Test
    void getAllEmails() {
        long id = 1;
        String emailAddress = "email@mail.com";
        Email email = getEmail(id, emailAddress);
        Collection<Email> collections = List.of(email);
        when(repo.findAll()).thenReturn(collections);

        List<Email> res = service.getAllEmails();

        Assertions.assertThat(res).isEqualTo(List.copyOf(collections));
    }

    @Test
    void findEmailTest() {
        long id = 1;
        String emailAddress = "email@mail.com";
        Email email = getEmail(id, emailAddress);
        when(repo.findByEmail(emailAddress)).thenReturn(Optional.of(email));

        Email res = service.findEmail(emailAddress);

        Assertions.assertThat(res).isEqualTo(email);
    }

    @Test
    void findEmailNorFoundTest() {
        String emailAddress = "email@mail.com";
        when(repo.findByEmail(emailAddress)).thenReturn(Optional.empty());
        Assertions.assertThatExceptionOfType(ResourceNotFountException.class).isThrownBy(()->service.findEmail(emailAddress));
    }

    Email getEmail(long id, String emailAddress){
        return new Email(id, emailAddress);
    }
}