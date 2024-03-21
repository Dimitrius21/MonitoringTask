package by.bzh.monitor.service;

import by.bzh.monitor.domain.dto.ComponentChangeDto;
import by.bzh.monitor.domain.dto.ComponentSimpleDto;
import by.bzh.monitor.domain.entity.Component;
import by.bzh.monitor.domain.entity.Email;
import by.bzh.monitor.exception.NotValidRequestParametersException;
import by.bzh.monitor.exception.ResourceNotFountException;
import by.bzh.monitor.repository.ComponentRepository;
import by.bzh.monitor.repository.EmailRepository;
import by.bzh.monitor.util.mapper.ComponentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ComponentService {
    private final ComponentRepository componentRepository;
    private final EmailRepository emailRepository;
    private final ComponentMapper mapper;

    public void deleteComponent(long componentId) {
        componentRepository.deleteById(componentId);
    }

    public Component createComponent(ComponentSimpleDto dto) {
        Component component = mapper.toComponent(dto);
        return componentRepository.save(component);
    }

    public Component changeComponent(ComponentChangeDto dto) {
        Component component = componentRepository.findById(dto.getComponentId())
                .orElseThrow(() -> new NotValidRequestParametersException(String.format("Component with id=%d not found", dto.getComponentId())));
        Email email = emailRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotValidRequestParametersException("The email is absent in system. Add it first"));
        List<Email> emailList = component.getEmailList();
        if (dto.getActionType() > 0 && !emailList.contains(email)) {
            emailList.add(email);
        } else if (dto.getActionType() < 0 && emailList.contains(email)) {
            emailList.remove(email);
        }
        return componentRepository.save(component);
    }

    public List<Component> getAllComponents() {
        List<Component> components = new ArrayList<>();
        componentRepository.findAll().forEach(components::add);
        return components;
    }

    public Component getComponent(long componentId) {
        return componentRepository.findById(componentId)
                .orElseThrow(() -> new ResourceNotFountException(String.format("Component with id=%d not found", componentId)));
    }
}
