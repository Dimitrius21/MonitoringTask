package by.bzh.monitor.service;

import by.bzh.monitor.domain.dto.ComponentChangeDto;
import by.bzh.monitor.domain.dto.ComponentSimpleDto;
import by.bzh.monitor.domain.entity.Component;
import by.bzh.monitor.domain.entity.Email;
import by.bzh.monitor.exception.NotValidRequestParametersException;
import by.bzh.monitor.repository.ComponentRepository;
import by.bzh.monitor.repository.EmailRepository;
import by.bzh.monitor.util.mapper.ComponentMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentServiceTest {
    @Mock
    ComponentRepository componentRepository;
    @Mock
    EmailRepository emailRepository;
    @Spy
    ComponentMapper mapper = Mappers.getMapper(ComponentMapper.class);

    @InjectMocks
    ComponentService service;

    @Test
    void deleteComponentTest() {
        long componentId = 1;
        service.deleteComponent(componentId);
        verify(componentRepository).deleteById(componentId);
    }

    @Test
    void createComponentTest() {
        long componentId = 1;
        ComponentSimpleDto dto = getComponentDto(componentId);
        Component component = getComponent(componentId, new ArrayList<>());
        when(componentRepository.save(component)).thenReturn(component);

        Component res = service.createComponent(dto);

        Assertions.assertThat(res).isEqualTo(component);
    }

    @Test
    void changeComponentAddEmailTest() {
        long componentId = 1;
        String emailAddress = "email@mail.com";
        ComponentChangeDto dto = getComponentChangeDto(componentId, emailAddress, 1);
        Email email = getEmail(1, emailAddress);
        Component componentInDb = getComponent(componentId, new ArrayList<>());
        Component component = getComponent(componentId, List.of(email));

        when(componentRepository.findById(componentId)).thenReturn(Optional.of(componentInDb));
        when(emailRepository.findByEmail(emailAddress)).thenReturn(Optional.of(email));
        when(componentRepository.save(component)).thenReturn(component);

        Component res = service.changeComponent(dto);

        Assertions.assertThat(res).isEqualTo(component);
    }

    @Test
    void changeComponentSubEmailTest() {
        long componentId = 1;
        String emailAddress = "email@mail.com";
        ComponentChangeDto dto = getComponentChangeDto(componentId, emailAddress, -1);
        Email email = getEmail(1, emailAddress);
        List<Email> emailList = new ArrayList<>();
        emailList.add(email);
        Component componentInDb = getComponent(componentId, emailList);
        Component component = getComponent(componentId, new ArrayList<>());

        when(componentRepository.findById(componentId)).thenReturn(Optional.of(componentInDb));
        when(emailRepository.findByEmail(emailAddress)).thenReturn(Optional.of(email));
        when(componentRepository.save(component)).thenReturn(component);

        Component res = service.changeComponent(dto);

        Assertions.assertThat(res).isEqualTo(component);
    }

    @Test
    void changeComponentAddEmailToNotExistComponentTest() {
        long componentId = 1;
        String emailAddress = "email@mail.com";
        ComponentChangeDto dto = getComponentChangeDto(componentId, emailAddress, 1);

        when(componentRepository.findById(componentId)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(NotValidRequestParametersException.class).isThrownBy(() -> service.changeComponent(dto));
    }

    @Test
    void getAllComponentsTest() {
        long componentId = 1;
        Component component = getComponent(componentId, new ArrayList<>());
        List<Component> components = List.of(component);
        when(componentRepository.findAll()).thenReturn(components);

        List<Component> res = service.getAllComponents();

        Assertions.assertThat(res).isEqualTo(components);
    }

    @Test
    void getComponentTest() {
        long componentId = 1;
        Component component = getComponent(componentId, new ArrayList<>());
        when(componentRepository.findById(componentId)).thenReturn(Optional.of(component));

        Component res = service.getComponent(componentId);

        Assertions.assertThat(res).isEqualTo(component);
    }

    private Component getComponent(long componentId, List<Email> emailList) {
        Component component = new Component();
        component.setComponentId(componentId);
        component.setDescription("description");
        component.setEmailList(emailList);
        return component;
    }

    private ComponentSimpleDto getComponentDto(long componentId) {
        ComponentSimpleDto dto = new ComponentSimpleDto();
        dto.setComponentId(componentId);
        dto.setDescription("description");
        return dto;
    }

    private Email getEmail(long id, String emailAddress) {
        Email email = new Email();
        email.setEmail(emailAddress);
        email.setId(id);
        return email;
    }

    private ComponentChangeDto getComponentChangeDto(long componentId, String emailAddress, int action) {
        ComponentChangeDto dto = new ComponentChangeDto();
        dto.setComponentId(componentId);
        dto.setEmail(emailAddress);
        dto.setActionType(action);
        return dto;
    }
}