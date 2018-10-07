package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@NoArgsConstructor
@lombok.ToString
@Entity
@Table(name = "car_parts")
public class CarPart implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 255, min = 6)
    private String name;

    @OneToMany(mappedBy = "part")
    private Set<VisitsParts> visits;

    public CarPart(String name){
        this.name = name;
    }

    public void addVisit(VisitsParts visit){
        this.visits.add(visit);
    }
}
