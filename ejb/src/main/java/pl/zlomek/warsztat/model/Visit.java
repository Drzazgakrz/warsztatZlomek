package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;
import pl.zlomek.warsztat.data.CarPartsRepository;
import pl.zlomek.warsztat.data.VisitsRepository;

import javax.inject.Inject;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "visits")
public class Visit implements Serializable {

    @Inject
    @Transient
    VisitsRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private LocalDate visitDate;

    @NotNull
    private VisitStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @OneToOne(fetch = FetchType.LAZY)
    private Overview overview;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Car car;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotNull
    @JoinTable(name = "visits_has_services", joinColumns = @JoinColumn(name = "service_id"),
    inverseJoinColumns = @JoinColumn(name = "visit_id"))
    private Set<Service> services;

    @OneToMany(mappedBy = "visit", fetch = FetchType.LAZY)
    private Set<VisitsParts> parts;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Visit(LocalDate date, Car car, Overview overview){

        this.visitDate = date;
        this.services = new HashSet<>();
        this.status = VisitStatus.ACCEPTED;
        this.car = car;
        this.parts = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.overview = overview;
    }

    public void addPartToVisit(CarPart part, int count, BigDecimal singlePrice){
        VisitsParts relation = new VisitsParts(this, part,count, singlePrice);
        this.parts.add(relation);
        part.addVisit(relation);
        repository.createVisitPart(relation);
    }
}
