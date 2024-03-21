package by.bzh.monitor.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "components")
public class Component {
    @Id
    @Column(name = "component_id")
    private long componentId;
    @Column(name = "description")
    private String description;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "mails",
            joinColumns = @JoinColumn(name = "component_id"),
            inverseJoinColumns = @JoinColumn(name = "email_id")
    )
    private List<Email> emailList;
}
