package by.bzh.monitor.util.mapper;

import by.bzh.monitor.domain.dto.ComponentSimpleDto;
import by.bzh.monitor.domain.entity.Component;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComponentMapper {
    @Mapping(target = "emailList", expression = "java(new java.util.ArrayList<>() )")
    public Component toComponent(ComponentSimpleDto dto);
}
