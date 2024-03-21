package by.bzh.monitor.service;

import by.bzh.monitor.domain.entity.Email;
import by.bzh.monitor.exception.ResourceNotFountException;
import by.bzh.monitor.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailRepository repo;

    @Transactional
    public void deleteEmail(String email) {
        repo.deleteByEmail(email);
    }

    public Email createEmail(String email) {
        Email emailObj = new Email();
        emailObj.setEmail(email);
        return repo.save(emailObj);
    }

    public List<Email> getAllEmails() {
        List<Email> emails = new ArrayList<>();
        repo.findAll().forEach(emails::add);
        return emails;
    }

    public Email findEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFountException("Indicated email not found"));
    }
}
