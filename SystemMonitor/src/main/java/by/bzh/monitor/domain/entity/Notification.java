package by.bzh.monitor.domain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "notifications")
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "happened")
    private LocalDateTime happened;
    @Column(name = "component_id")
    private long componentId;
    @Column(name = "level")
    private int level;
    @Column(name = "description")
    private String description;

}
