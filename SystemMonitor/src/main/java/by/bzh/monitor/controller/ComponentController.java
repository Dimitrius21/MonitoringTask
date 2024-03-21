package by.bzh.monitor.controller;

import by.bzh.monitor.domain.dto.ComponentChangeDto;
import by.bzh.monitor.domain.dto.ComponentSimpleDto;
import by.bzh.monitor.domain.entity.Component;
import by.bzh.monitor.service.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/component")
public class ComponentController {
    public final ComponentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody ComponentSimpleDto dto) {
        service.createComponent(dto);
    }

    @GetMapping
    public ResponseEntity<List<Component>> getAll() {
        return new ResponseEntity<>(service.getAllComponents(), HttpStatus.OK);
    }

    @GetMapping("/{componentId}")
    public ResponseEntity<Component> getComponent(@PathVariable long componentId) {
        return new ResponseEntity<>(service.getComponent(componentId), HttpStatus.OK);
    }

    @DeleteMapping("/{componentId}")
    public void delete(@PathVariable long componentId) {
        service.deleteComponent(componentId);
    }

    @PutMapping
    public void changeEmailsForMails(@RequestBody ComponentChangeDto dto) {
        service.changeComponent(dto);
    }
}
