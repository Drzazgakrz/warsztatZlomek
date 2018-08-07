package pl.zlomek.warsztat.model;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private long clientId;

    @NotNull
    @Size(max = 30, min = 3)
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Size(max = 30, min = 2)
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Size(max = 30, min = 6)
    private String email;

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

    @NotNull
    @Size(max=256, min = 256)
    private String password;

    @ManyToMany
    @JoinTable(name = "clients_has_cars",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private List<Company> companies;

    @ManyToMany
    @NotNull
    @JoinTable(name = "clients_has_cars",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private List<Car> cars;

    public Client(String firstName, String lastName, String email, String phoneNumber, String cityName,
                  String streetName, String buildNum, String aptNum, String zipCode, String password,
                  List<Company> companies, List<Car> cars){
        this.aptNum = aptNum;
        this.buildNum = buildNum;
        this.cityName = cityName;
        this.companies = companies;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this. streetName = streetName;
        this.zipCode = zipCode;
        SHA3.DigestSHA3 sha3 = new SHA3.Digest256();
        sha3.update(password.getBytes());
        this.password = sha3.digest().toString();
        this.cars = cars;
    }
}
