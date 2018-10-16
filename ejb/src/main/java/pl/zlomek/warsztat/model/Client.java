package pl.zlomek.warsztat.model;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@lombok.ToString
@lombok.NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private long clientId;

    @NotNull
    @Size(max = 15, min = 9)
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @Size(max = 20, min = 2)
    @Column(name = "city_name")
    private String cityName;

    @NotNull
    @Size(max = 40, min = 3)
    @Column(name = "street_name")
    private String streetName;

    @NotNull
    @Size(max = 5, min = 1)
    @Column(name = "build_number")
    private String buildNum;

    @Size(max = 5)
    @Column(name = "apartment_number")
    private String aptNum;

    @NotNull
    @Size(max = 6, min = 6)
    @Column(name = "zip_code")
    private String zipCode;

    @ManyToMany(
            fetch = FetchType.LAZY
    )
    private Set<Company> companies;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "owner"
    )
    private Set<CarsHasOwners> cars;

    private ClientStatus status;

    public Client(String firstName, String lastName, String email, String phoneNumber, String cityName,
                  String streetName, String buildNum, String aptNum, String zipCode, String password,
                  String accessToken){
        super(email, firstName, lastName, password, LocalDateTime.now(), LocalDateTime.now());
        this.aptNum = aptNum;
        this.buildNum = buildNum;
        this.cityName = cityName;
        this.phoneNumber = phoneNumber;
        this. streetName = streetName;
        this.zipCode = zipCode;
        this.cars = new HashSet<>();
        this.companies = new HashSet<>();
        this.accessToken = accessToken;
        this.status = ClientStatus.ACTIVE;
    }

    public Object[] checkCar(Car car){
        return cars.stream().filter(clientCar-> (clientCar.equals(car) && clientCar.getStatus()!= OwnershipStatus.FORMER_OWNER)).toArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return clientId == client.clientId &&
                Objects.equals(phoneNumber, client.phoneNumber) &&
                Objects.equals(cityName, client.cityName) &&
                Objects.equals(streetName, client.streetName) &&
                Objects.equals(buildNum, client.buildNum) &&
                Objects.equals(aptNum, client.aptNum) &&
                Objects.equals(zipCode, client.zipCode) &&
                Objects.equals(companies, client.companies) &&
                Objects.equals(cars, client.cars);
    }
}
