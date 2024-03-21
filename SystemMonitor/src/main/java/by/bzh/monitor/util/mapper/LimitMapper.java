package by.bzh.monitor.util.mapper;

import by.bzh.monitor.domain.dto.LimitInDto;
import by.bzh.monitor.domain.dto.NotificationInDto;
import by.bzh.monitor.domain.entity.Limit;
import by.bzh.monitor.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LimitMapper {
    @Mapping(target = "quantity", expression = "java(0)")
    public Limit toLimit(LimitInDto dto);
}
