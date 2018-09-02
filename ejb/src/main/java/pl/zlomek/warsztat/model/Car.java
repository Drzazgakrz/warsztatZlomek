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
    @Column(name = "registration_number")
    private String registrationNumber;

    @NotNull
    @Size(min = 2, max = 30)
    private String model;

    @NotNull
    @Size(min = 4, max = 4)
    @Column(name = "prod_year")
    private int prodYear;

    @NotNull
    @Size(min = 17, max = 17)
    @Column(name = "vin_number")
    private String vin;

    @ManyToOne
    @NotNull
    CarBrand brand;

    @NotNull
    @ManyToMany
    @JoinTable(name = "clients_has_cars",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    List<Client> owners;

    @ManyToMany
    @JoinTable(name = "company_has_cars",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private List<Company> companiesCars;

    public Car(String registrationNumber, String model, int prodYear, CarBrand brand, List<Client> owners,
               List<Company> companiesCars){
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
        this.prodYear = prodYear;
        this.owners = owners;
        this.companiesCars = companiesCars;
    }

}
