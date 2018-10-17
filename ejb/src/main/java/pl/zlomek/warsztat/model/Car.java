package pl.zlomek.warsztat.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@Entity
@Table(name = "cars")
public class Car implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 2, max = 30)
    private String model;

    @NotNull
    @Column(name = "prod_year")
    private int prodYear;

    @NotNull
    @Size(min = 17, max = 17)
    @Column(name = "vin_number")
    private String vin;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    CarBrand brand;

    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "car")
    Set<CarsHasOwners> owners;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "car")
    private Set<CompaniesHasCars> companiesCars;

    @OneToMany(mappedBy = "car")
    private Set<Visit> visits;

    public Car(String vin, String model, int prodYear, CarBrand brand){
        this.brand = brand;
        this.model = model;
        this.prodYear = prodYear;
        this.owners = new HashSet<>();
        this.vin = vin;
        this.companiesCars = new HashSet<>();
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
