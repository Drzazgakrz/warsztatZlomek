package pl.zlomek.warsztat.model;


import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;


@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@NoArgsConstructor
@Entity
@Table(name = "overviews")
public class Overview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "overview_date")
    private Date overviewDate;

    @ManyToOne
    @NotNull
    private Car car;

    public Overview(Date date, Car car){
        this.overviewDate = date;
        this.car = car;
    }
}
