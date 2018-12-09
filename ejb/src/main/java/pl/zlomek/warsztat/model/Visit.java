package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;
import pl.zlomek.warsztat.data.CarPartsRepository;
import pl.zlomek.warsztat.data.ServicesRepository;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @NotNull
    private VisitStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "overview_id")
    private Overview overview;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "visit")
    @NotNull
    private Set<VisitsHasServices> services;

    @OneToMany(mappedBy = "visit", fetch = FetchType.LAZY)
    private Set<VisitsParts> parts;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "visit_finished")
    private LocalDate visitFinished;

    public Visit(LocalDateTime date, Car car, Overview overview, Client client){

        this.visitDate = date;
        this.services = new HashSet<>();
        this.status = VisitStatus.NEW;
        this.car = car;
        this.parts = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.overview = overview;
        this.client = client;
    }

    public VisitsParts addPartToVisit(CarPart part, int count, BigDecimal singlePrice){
        VisitsParts relation = new VisitsParts(this, part,count, singlePrice);
        this.parts.add(relation);
        part.addVisit(relation);
        return relation;
    }

    public VisitsHasServices addServiceToVisit(Service service, int count, BigDecimal singlePrice){
        VisitsHasServices relation = new VisitsHasServices( service, this,count, singlePrice);
        this.services.add(relation);
        service.getVisits().add(relation);
        return relation;
    }
}
