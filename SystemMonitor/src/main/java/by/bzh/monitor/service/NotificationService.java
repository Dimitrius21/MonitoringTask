package by.bzh.monitor.service;

import by.bzh.monitor.domain.RabbitMessage;
import by.bzh.monitor.domain.dto.NotificationInDto;
import by.bzh.monitor.domain.entity.Component;
import by.bzh.monitor.domain.entity.Email;
import by.bzh.monitor.domain.entity.Limit;
import by.bzh.monitor.domain.entity.Notification;
import by.bzh.monitor.exception.ResourceNotFountException;
import by.bzh.monitor.repository.ComponentRepository;
import by.bzh.monitor.repository.NotificationRepository;
import by.bzh.monitor.util.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;
    private final ComponentRepository componentRepository;
    private final NotificationMapper mapper;
    private final LimitService limitService;
    private final RabbitService rabbitService;

    public Notification getNotification(long id) {
        Notification note = repo.findById(id)
                .orElseThrow(()-> new ResourceNotFountException("Notification with id " + id + " not found"));
        return note;
    }

    public List<Notification> getAll(Pageable pageable, long componentId, int level) {
        if (componentId == -1 && level == -1) {
            return repo.findAll(pageable).toList();
        }
        Notification note = new Notification();
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id", "happened", "description");
        if (componentId == -1) {
            matcher = matcher.withIgnorePaths("componentId");
        } else {
            note.setComponentId(componentId);
        }
        if (level == -1) {
            matcher = matcher.withIgnorePaths("level");
        } else {
            note.setLevel(level);
        }
        Example<Notification> noteExample = Example.of(note, matcher);
        return repo.findAll(noteExample, pageable).toList();
    }

    public Notification createNote(NotificationInDto dto) {
        Notification note = mapper.toNotification(dto);
        note = repo.save(note);
        if (limitService.hasLimit(note.getComponentId(), note.getLevel())) {
            Limit limit = limitService.changeQuantity(note.getComponentId(), note.getLevel(), 1);
            if (limit.getQuantity() >= limit.getThreshold()) {
                sendToRabbit(note.getComponentId(), note.getLevel());
            }
        }
        return note;
    }

    private void sendToRabbit(long componentId, int level) {
        Optional<Component> optionalComponent = componentRepository.findById(componentId);
        if (optionalComponent.isEmpty()) {
            //Additional check for some reason, we must always get component (check in hasLimit)
            log.info("For component {} reach/exceed level = {}, but not email(s) for notification", componentId, level);
            return;
        }
        Component component = optionalComponent.get();
        List<String> emailList = component.getEmailList().stream().map(Email::getEmail).toList();
        if (emailList.isEmpty()) {
            log.warn("For component {} reach/exceed level = {}, but not email(s) for notification", componentId, level);
            return;
        }
        RabbitMessage mes = new RabbitMessage();
        mes.setMessage(String.format("For component %d reach/exceed level = %d", componentId, level));
        mes.setEmailList(emailList);
        rabbitService.send(mes);
        log.info("For component {} reach/exceed level = {}", componentId, level);
    }

}
