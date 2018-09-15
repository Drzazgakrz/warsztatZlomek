package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "companies_data")
public class CompanyData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 14,max = 14)
    @Column(name = "NIP")
    private String nip;

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

    @OneToMany (mappedBy = "companyData")
    private Set<Invoice> invoices;

    public CompanyData(String nip, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode, Set<Invoice> invoices) {
        this.nip = nip;
        this.companyName = companyName;
        this.cityName = cityName;
        this.streetName = streetName;
        this.buildingNum = buildingNum;
        this.aptNum = aptNum;
        this.zipCode = zipCode;
        this.invoices = invoices;
    }
}
