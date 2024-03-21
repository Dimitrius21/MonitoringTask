package by.bzh.monitor.domain.dto;

import lombok.Data;

@Data
public class LimitInDto {
    private long componentId;
    private int level;
    private int threshold;
}
