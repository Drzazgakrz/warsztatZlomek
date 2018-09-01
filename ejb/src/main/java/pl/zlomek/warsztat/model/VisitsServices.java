package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "visits_has_services")
public class VisitsServices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Visit visit;

    @ManyToOne(cascade = CascadeType.ALL)
    private Service service;

    private double singleServicePrice;

    private String description;
}
