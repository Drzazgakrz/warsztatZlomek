package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.ToString
@Entity
@Table(name = "car_parts")
public class CarParts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 255, min = 6)
    private String name;

    @ManyToMany
    @JoinTable(name = "visits_has_parts",joinColumns = @JoinColumn(name = "visit_id"),
    inverseJoinColumns = @JoinColumn(name = "part_id"))
    private List<Visit> visits;
}
