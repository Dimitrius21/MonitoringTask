package by.bzh.monitor.service;

import by.bzh.monitor.domain.dto.NotificationInDto;
import by.bzh.monitor.domain.entity.Component;
import by.bzh.monitor.domain.entity.Email;
import by.bzh.monitor.domain.entity.Limit;
import by.bzh.monitor.domain.entity.Notification;
import by.bzh.monitor.repository.ComponentRepository;
import by.bzh.monitor.repository.NotificationRepository;
import by.bzh.monitor.util.mapper.NotificationMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private NotificationRepository repo;
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private LimitService limitService;
    @Mock
    private RabbitService rabbitService;
    @Spy
    private NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

    @InjectMocks
    NotificationService service;

    @Test
    void getNotificationTest() {
        long id = 1;
        Notification note = getNotification(id, LocalDateTime.now(), 1, 2, "event1");
        when(repo.findById(id)).thenReturn(Optional.of(note));
        Notification res = service.getNotification(id);
        Assertions.assertThat(res).isEqualTo(note);
    }

    @Test
    void getAllTest() {
        long componentId = 1;
        int level = 1;
        Notification note = getNotification(0, null, componentId, level, null);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id", "happened", "description");
        Example<Notification> noteExample = Example.of(note, matcher);
        Pageable pageable = PageRequest.of(1, 20);
        Page<Notification> page = Page.empty();
        when(repo.findAll(noteExample, pageable)).thenReturn(page);

        List<Notification> res = service.getAll(pageable, componentId,level);

        Assertions.assertThat(res).hasSize(0);
    }

    @Test
    void createNoteWithSendToRabbitTest() {
        long id = 0;
        long componentId = 1;
        int level = 2;
        LocalDateTime now = LocalDateTime.now();
        Notification noteForSave = getNotification(id, now, componentId, level, "event1");
        Notification note = getNotification(1, now, componentId, level, "event1");
        NotificationInDto dto = getNotificationInDto(now, componentId, level, "event1");
        Limit limit = new Limit(componentId, level, 1, 1);
        Component component = getComponent(componentId);

        when(repo.save(noteForSave)).thenReturn(note);
        when(limitService.hasLimit(componentId, level)).thenReturn(true);
        when(limitService.changeQuantity(componentId, level, 1)).thenReturn(limit);
        when(componentRepository.findById(componentId)).thenReturn(Optional.of(component));

        Notification res = service.createNote(dto);

        Assertions.assertThat(res).isEqualTo(note);
        verify(rabbitService).send(any());
    }

    @Test
    void createNoteWithoutSendToRabbitTest() {
        long id = 0;
        long componentId = 1;
        int level = 2;
        LocalDateTime now = LocalDateTime.now();
        Notification noteForSave = getNotification(id, now, componentId, level, "event1");
        Notification note = getNotification(1, now, componentId, level, "event1");
        NotificationInDto dto = getNotificationInDto(now, componentId, level, "event1");
        Limit limit = new Limit(componentId, level, 1, 5);
        Component component = getComponent(componentId);

        when(repo.save(noteForSave)).thenReturn(note);
        when(limitService.hasLimit(componentId, level)).thenReturn(true);
        when(limitService.changeQuantity(componentId, level, 1)).thenReturn(limit);

        Notification res = service.createNote(dto);

        Assertions.assertThat(res).isEqualTo(note);
        verifyNoInteractions(rabbitService);
    }

    private Notification getNotification(long id, LocalDateTime happened, long componentId, int level, String description) {
        Notification note = new Notification();
        note.setId(id);
        note.setHappened(happened);
        note.setLevel(level);
        note.setComponentId(componentId);
        note.setDescription(description);
        return note;
    }

    private NotificationInDto getNotificationInDto(LocalDateTime happened, long componentId, int level, String description) {
        NotificationInDto dto = new NotificationInDto();
        dto.setHappened(happened);
        dto.setLevel(level);
        dto.setComponentId(componentId);
        dto.setDescription(description);
        return dto;
    }

    private Component getComponent(long componentId) {
        Component component = new Component();
        component.setComponentId(componentId);
        Email email = new Email();
        email.setEmail("email@mail.com");
        List<Email> emailList = List.of(email);
        component.setEmailList(emailList);
        return component;
    }

}