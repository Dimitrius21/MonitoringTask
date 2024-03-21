package by.bzh.monitor.controller;

import by.bzh.monitor.domain.dto.LimitInDto;
import by.bzh.monitor.domain.entity.Limit;
import by.bzh.monitor.service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/limit")
public class LimitController {
    private final LimitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody LimitInDto dto) {
        service.createLimit(dto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody LimitInDto dto) {
        service.updateLimit(dto);
    }

    @PutMapping("/reset/{componentId}/{level}")
    @ResponseStatus(HttpStatus.OK)
    public void reset(@PathVariable long componentId, @PathVariable int level) {
        service.changeQuantity(componentId, level, 0);
    }

    @GetMapping("/{componentId}/{level}")
    public ResponseEntity<Limit> getOne(@PathVariable long componentId, @PathVariable int level) {
        Limit limit = service.getLimit(componentId, level);
        return new ResponseEntity<>(limit, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Limit>> getAll() {
        return new ResponseEntity<>(service.getAllLimits(), HttpStatus.OK);
    }

    @DeleteMapping("/{componentId}/{level}")
    public void delete(@PathVariable long componentId, @PathVariable int level) {
        service.deleteLimit(componentId, level);
    }

}
