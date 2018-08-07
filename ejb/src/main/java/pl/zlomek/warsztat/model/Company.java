package pl.zlomek.warsztat.model;



import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 14,max=14)
    private String NIP;

    @NotNull
    @Size(max = 30, min = 6)
    private String email;

    @NotNull
    @Size(min = 2, max = 40)
    @Column(name = "company_name")
    private String companyName;

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
    @Column(name = "building_number")
    private String buildingNum;

    @Size(max = 5)
    @Column(name = "apartment_number")
    private String aptNum;

    @NotNull
    @Size(max = 6, min = 6)
    @Column(name = "zip_code")
    private String zipCode;

    @ManyToMany
    @JoinTable(name = "company_has_employees",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private List<Client> employees;

    @ManyToMany
    @JoinTable(name = "company_has_cars",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private List<Car> cars;

    public Company(String NIP, String email, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode, List<Car> cars){
        this.NIP = NIP;
        this.email = email;
        this.companyName = companyName;
        this.cityName = cityName;
        this.streetName = streetName;
        this.buildingNum = buildingNum;
        this.aptNum = aptNum;
        this.zipCode = zipCode;
        this.cars = cars;
    }
}
