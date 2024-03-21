package by.bzh.monitor.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(LimitKey.class)
@Table(name = "limits")
public class Limit {
    @Id
    private long componentId;
    @Id
    private int level;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "threshold")
    private int threshold;
}
