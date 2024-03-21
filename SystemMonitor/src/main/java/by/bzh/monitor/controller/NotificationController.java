package by.bzh.monitor.controller;

import by.bzh.monitor.domain.dto.NotificationInDto;
import by.bzh.monitor.domain.entity.Notification;
import by.bzh.monitor.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/note")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable long id) {
        Notification note = service.getNotification(id);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotification(Pageable pageable,
                                                                 @RequestParam(defaultValue = "-1") long component,
                                                                 @RequestParam(defaultValue = "-1") int level) {
        List<Notification> notes = service.getAll(pageable, component, level);
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody NotificationInDto dto) {
        Notification note = service.createNote(dto);
        return new ResponseEntity<>(note, HttpStatus.CREATED);
    }
}
