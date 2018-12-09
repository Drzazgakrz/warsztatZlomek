package pl.zlomek.warsztat.model;


import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;


@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@NoArgsConstructor
@Entity
@Table(name = "overviews")
public class Overview implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "overview_date")
    private LocalDateTime overviewDate;

    @ManyToOne
    @NotNull
    private Car car;

    @Column(name = "overview_last_day")
    private LocalDate overviewLastDay;

    public Overview(LocalDateTime date, Car car){
        this.overviewDate = date;
        this.car = car;
    }
    public void addTerminateOverview(int years){
        this.overviewLastDay = overviewDate.plusYears(years).toLocalDate();
    }
}
