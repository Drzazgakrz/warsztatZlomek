package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "visits")
public class Visit {
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
    private List<Service> services;

    public Visit(Date date, VisitStatus status, Employee employee, Car car, List<Service> services){
        this.visitDate = date;
        this.services = services;
        this.status = status;
        this.employee = employee;
        this.car = car;
    }
}