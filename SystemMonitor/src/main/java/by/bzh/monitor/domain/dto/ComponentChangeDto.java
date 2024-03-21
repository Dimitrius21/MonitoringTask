package by.bzh.monitor.domain.dto;

import lombok.Data;

@Data
public class ComponentChangeDto {
    private long componentId;
    private String email;
    private int actionType;
}
