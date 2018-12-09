package pl.zlomek.warsztat.model;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@Entity
@Table(name = "cars")
public class Car implements Serializable {
    @Transient
    int maxYear = LocalDate.now().getYear();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z]{1}+[A-Za-z0-9/]{1,}")
    private String model;

    @NotNull
    @Min(value = 1930)
    @Column(name = "prod_year")
    private int prodYear;

    @NotNull
    @Size(min = 17, max = 17)
    @Column(name = "vin_number")
    private String vin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    @NotNull
    CarBrand brand;

    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "car")
    Set<CarsHasOwners> owners;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "car")
    private Set<CompaniesHasCars> companiesCars;

    @OneToMany(mappedBy = "car")
    private Set<Visit> visits;

    @OneToMany(mappedBy = "car")
    private Set<Overview> overviews;

    public Car(String vin, String model, int prodYear, CarBrand brand){
        this.brand = brand;
        this.model = model;
        this.prodYear = prodYear;
        this.owners = new HashSet<>();
        this.vin = vin;
        this.companiesCars = new HashSet<>();
        this.overviews = new HashSet<>();
    }

    public CarsHasOwners addCarOwner(Client client, OwnershipStatus status, String registrationNumber){
        CarsHasOwners cho = new CarsHasOwners(this, client, status, registrationNumber);
        this.owners.add(cho);
        client.getCars().add(cho);
        return cho;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return Objects.equals(vin, car.vin);

    }
}
