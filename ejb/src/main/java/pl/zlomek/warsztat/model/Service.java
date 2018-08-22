package pl.zlomek.warsztat.model;


import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@NoArgsConstructor
@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "service_name")
    @NotNull
    private String name;

    @ManyToMany
    @NotNull
    @JoinTable(name = "visits_has_services", joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "visit_id"))
    private List<Visit> visits;
}
