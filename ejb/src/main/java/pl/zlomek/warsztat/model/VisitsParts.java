package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "visits_has_parts")
public class VisitsParts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Visit visit;

    @ManyToOne(cascade = CascadeType.ALL)
    private CarParts part;

    private double singlePartPrice;

    private int count;
}
