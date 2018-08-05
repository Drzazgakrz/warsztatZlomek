package pl.zlomek.warsztat.model;



import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
@Entity
@Table(name = "companies")
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String NIP;

    @NotNull
    private String email;

    @NotNull
    private String companyName;

    @NotNull
    private String cityName;

    @NotNull
    private String streetName;

    @NotNull
    private String buildingNum;

    private String aptNum;

    @NotNull
    private String zipCode;

    @ManyToMany
    @JoinTable(name = "company_has_employees",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private List<Client> employees;

    public Company(String NIP, String email, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode){
        this.NIP = NIP;
        this.email = email;
        this.companyName = companyName;
        this.cityName = cityName;
        this.streetName = streetName;
        this.buildingNum = buildingNum;
        this.aptNum = aptNum;
        this.zipCode = zipCode;
    }
}
