package by.bzh.monitor.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationInDto {
    private LocalDateTime happened;
    private long componentId;
    private int level;
    private String description;
}
