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
    @Size(min = 7, max = 8)
    @Column(name = "registration_number")
    private String registrationNumber;

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

    @ManyToOne
    @NotNull
    CarBrand brand;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "owner")
    Set<CarsHasOwners> owners;

    @ManyToMany
    private Set<Company> companiesCars;

    public Car(String registrationNumber, String vin, String model, int prodYear, CarBrand brand, Client client){
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
        this.prodYear = prodYear;
        this.owners = new HashSet<>();
        this.vin = vin;
        this.companiesCars = new HashSet<>();
    }

    public CarsHasOwners addCarOwner(Client client){
        CarsHasOwners cho = new CarsHasOwners(this, client, OwnershipStatus.CURRENT_OWNER);
        this.owners.add(cho);
        client.getCars().add(cho);
        return cho;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return id == car.id &&
                prodYear == car.prodYear &&
                Objects.equals(registrationNumber, car.registrationNumber) &&
                Objects.equals(model, car.model) &&
                Objects.equals(vin, car.vin) &&
                Objects.equals(brand, car.brand) &&
                Objects.equals(owners, car.owners) &&
                Objects.equals(companiesCars, car.companiesCars);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, registrationNumber, model, prodYear, vin, brand, owners, companiesCars);
    }
}
