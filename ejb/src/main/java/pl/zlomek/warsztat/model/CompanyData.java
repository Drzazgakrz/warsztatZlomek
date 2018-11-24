package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
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

    public CompanyData(Company company){
        super(company.getNip(), company.getCompanyName(), company.getCityName(), company.getStreetName(),
                company.getBuildingNum(), company.getAptNum(), company.getZipCode());
        this.invoices = new HashSet<>();
    }

    public CompanyData(CompanyDataBuffer buffer){
        super(buffer.getNip(), buffer.getCompanyName(), buffer.getCityName(), buffer.getStreetName(),
                buffer.getBuildingNum(), buffer.getAptNum(), buffer.getZipCode());
        this.invoices = new HashSet<>();
    }

    public boolean compareCompanies(CompanyModel model){
        boolean result = model.getNip().equals(this.nip)&&model.getCompanyName().equals(this.companyName);
        result = result && model.getBuildingNum().equals(this.buildingNum) && model.getCityName().equals(this.cityName);
        if(this.aptNum != null)
            result = result && this.aptNum.equals(model.getAptNum());
        return result && model.getStreetName().equals(this.streetName) && model.getZipCode().equals(this.zipCode);
    }
}
