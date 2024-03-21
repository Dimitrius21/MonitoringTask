package by.bzh.monitor.controller;

import by.bzh.monitor.domain.dto.ComponentChangeDto;
import by.bzh.monitor.domain.dto.ComponentSimpleDto;
import by.bzh.monitor.domain.dto.LimitInDto;
import by.bzh.monitor.domain.entity.Component;
import by.bzh.monitor.domain.entity.Email;
import by.bzh.monitor.domain.entity.Limit;
import by.bzh.monitor.domain.entity.Notification;
import by.bzh.monitor.service.ComponentService;
import by.bzh.monitor.service.EmailService;
import by.bzh.monitor.service.LimitService;
import by.bzh.monitor.service.NotificationService;
import by.bzh.monitor.util.mapper.LimitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class GraphqlController {
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final LimitService limitService;
    private final ComponentService componentService;
    private final LimitMapper limitMapper;

    @Value("${spring.data.web.pageable.default-page-size}")
    private String defaultPageSize;
    private final static String USER = "ROLE_USER";
    private final static String ADMIN = "ROLE_ADMIN";

    @QueryMapping(value = "notificationById")
    public Notification getNotificationById(@Argument(name = "id") Integer id, Authentication authentication) {
        if (!isAuthorized(authentication, USER)) {
            throw new AccessDeniedException("not authorized");
        }
        return notificationService.getNotification(id);
    }

    @QueryMapping
    public List<Notification> notifications(@Argument Integer componentId, @Argument Integer level, @Argument Integer page,
                                            @Argument Integer size, Authentication authentication) {
        if (!isAuthorized(authentication, USER)) {
            throw new AccessDeniedException("not authorized");
        }
        int sizeOfSelect = Objects.nonNull(size) ? size.intValue() : Integer.parseInt(defaultPageSize);
        int pageOfSelect = Objects.nonNull(page) ? page.intValue() : 0;
        Pageable pageable = PageRequest.of(pageOfSelect, sizeOfSelect, Sort.Direction.DESC, "happened");
        int id = Objects.nonNull(componentId) ? componentId.intValue() : -1;
        int lvl = Objects.nonNull(level) ? level.intValue() : -1;
        return notificationService.getAll(pageable, id, lvl);
    }

    @QueryMapping
    public List<Email> emails(Authentication authentication) {
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        return emailService.getAllEmails();
    }

    @SchemaMapping
    public List<Email> emails(Component component) {
        return componentService.getComponent(component.getComponentId()).getEmailList();
    }

    @QueryMapping
    public List<Limit> limits(Authentication authentication) {
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        return limitService.getAllLimits();
    }

    @QueryMapping(value = "limitOne")
    public Limit getOneLimit(@Argument Integer componentId, @Argument Integer level, Authentication authentication) {
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        return limitService.getLimit(componentId, level);
    }

    @QueryMapping
    public List<Component> components(Authentication authentication) {
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        return componentService.getAllComponents();
    }

    @QueryMapping(value = "componentById")
    public Component getOneComponent(@Argument Integer componentId, Authentication authentication) {
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        return componentService.getComponent(componentId);
    }


    @MutationMapping(name = "createComponent")
    public Component addComponent(@Argument(name = "component") ComponentSimpleDto component, Authentication authentication){
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        return componentService.createComponent(component);
    }

    @MutationMapping(name = "createEmail")
    public Email addEmail(@Argument String email, Authentication authentication){
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        return emailService.createEmail(email);
    }

    @MutationMapping(name = "createLimit")
    public Limit addLimit(@Argument LimitInDto limit, Authentication authentication){
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        return limitService.createLimit(limit);
    }

    @MutationMapping(name = "deleteEmail")
    public void deleteEmail(@Argument String email, Authentication authentication){
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        emailService.deleteEmail(email);
    }

    @MutationMapping(name = "deleteLimit")
    public void deleteLimit(@Argument Integer componentId, @Argument Integer level, Authentication authentication) {
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        limitService.deleteLimit(componentId, level);
    }

    @MutationMapping(name = "deleteComponent")
    public void deleteComponent(@Argument Integer componentId, Authentication authentication) {
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        componentService.deleteComponent(componentId);
    }

    @MutationMapping(name = "updateLimit")
    public Limit updateLimit(@Argument LimitInDto limit, Authentication authentication){
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        limitService.updateLimit(limit);
        return limitMapper.toLimit(limit);
    }

    @MutationMapping(name = "changeEmailsForMails")
    public void changeEmailsForMails(@Argument ComponentChangeDto emailChange, Authentication authentication){
        if (!isAuthorized(authentication, ADMIN)) {
            throw new AccessDeniedException("not authorized");
        }
        componentService.changeComponent(emailChange);
    }

    private boolean isAuthorized(Authentication authentication, String role) {
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return authorities.stream().anyMatch(a -> a.equals(role));
    }
}
