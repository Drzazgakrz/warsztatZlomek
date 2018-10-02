package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "companies_data")
public class CompanyData extends CompanyModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @OneToMany (mappedBy = "companyData")
    private Set<Invoice> invoices;

    public CompanyData(String nip, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode) {
        super(nip, companyName, cityName, streetName, buildingNum, aptNum, zipCode);
        this.invoices = invoices;
    }
}
