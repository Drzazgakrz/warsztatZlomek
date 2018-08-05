package pl.zlomek.warsztat.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 7, max = 8)
    private String registrationNumber;

    @NotNull
    @Size(min = 2, max = 30)
    private String model;

    @NotNull
    @Size(min = 4, max = 4)
    private int prodYear;

    @ManyToOne
    @NotNull
    CarBrand brand;

}
