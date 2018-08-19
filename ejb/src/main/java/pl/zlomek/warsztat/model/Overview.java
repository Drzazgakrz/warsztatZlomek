package pl.zlomek.warsztat.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
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
