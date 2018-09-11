package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "visits")
public class Visit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private Date visitDate;

    @NotNull
    private VisitStatus status;

    @NotNull
    @ManyToOne
    private Employee employee;

    @NotNull
    @ManyToOne
    private Car car;

    @ManyToMany
    @NotNull
    @JoinTable(name = "visits_has_services", joinColumns = @JoinColumn(name = "service_id"),
    inverseJoinColumns = @JoinColumn(name = "visit_id"))
    private Set<Service> services;

    @OneToMany(mappedBy = "visit")
    private Set<VisitsParts> parts;


    public Visit(Date date, Car car){

        this.visitDate = date;
        this.services = new HashSet<>();
        this.status = VisitStatus.ACCEPTED;
        this.car = car;
        this.parts = new HashSet<>();
    }
}
