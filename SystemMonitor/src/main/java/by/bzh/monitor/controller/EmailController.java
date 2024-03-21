package by.bzh.monitor.controller;

import by.bzh.monitor.domain.entity.Email;
import by.bzh.monitor.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailController {
    public  final EmailService service;
    @PostMapping
    public ResponseEntity<Email> create(@RequestParam String email){
        return new ResponseEntity<>(service.createEmail(email), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Email>>getAll(){
        return new ResponseEntity<>(service.getAllEmails(), HttpStatus.OK);
    }

    @DeleteMapping
    public void delete(@RequestParam String email){
        service.deleteEmail(email);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Email>getEmail(@PathVariable String email) {
        return new ResponseEntity<>(service.findEmail(email), HttpStatus.OK);
    }
}
