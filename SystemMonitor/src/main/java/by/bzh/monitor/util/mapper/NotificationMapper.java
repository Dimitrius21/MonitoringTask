package by.bzh.monitor.util.mapper;

import by.bzh.monitor.domain.dto.NotificationInDto;
import by.bzh.monitor.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "id", expression = "java(0)")
    public Notification toNotification(NotificationInDto dto);
}
