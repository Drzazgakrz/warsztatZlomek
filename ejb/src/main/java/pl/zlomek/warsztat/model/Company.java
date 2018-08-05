package pl.zlomek.warsztat.model;



import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Size(min = 14,max=14)
    private String NIP;

    @NotNull
    @Size(max = 30, min = 6)
    private String email;

    @NotNull
    @Size(min = 2, max = 40)
    private String companyName;

    @NotNull
    @Size(max = 20, min = 2)
    private String cityName;

    @NotNull
    @Size(max = 40, min = 3)
    private String streetName;

    @NotNull
    @Size(max = 5, min = 1)
    private String buildingNum;

    @Size(max = 5)
    private String aptNum;

    @NotNull
    @Size(max = 6, min = 6)
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
