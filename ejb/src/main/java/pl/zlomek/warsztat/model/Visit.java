package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;
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

    @ManyToOne
    private Employee employee;

    @OneToOne
    private Overview overview;

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

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public Visit(Date date, Car car, Overview overview){

        this.visitDate = date;
        this.services = new HashSet<>();
        this.status = VisitStatus.ACCEPTED;
        this.car = car;
        this.parts = new HashSet<>();
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.overview = overview;
    }

    public void addPartToVisit(CarPart part, int count, BigDecimal singlePrice){
        VisitsParts relation = new VisitsParts(this, part,count, singlePrice);
        this.parts.add(relation);
        part.addVisit(relation);
    }
}
