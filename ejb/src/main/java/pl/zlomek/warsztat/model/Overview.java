package pl.zlomek.warsztat.model;


import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
    private Date overviewDate;

    @ManyToOne
    @NotNull
    private Car car;

    private Date overviewLastDay;

    public Overview(Date date, Car car){
        this.overviewDate = date;
        this.car = car;
    }
    public void addTerminateOverview(int years){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.overviewDate);
        calendar.add(Calendar.YEAR, years);
        this.overviewLastDay = calendar.getTime();
    }
}
