package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "company_data_buffer")

public class CompanyDataBuffer extends CompanyModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @OneToMany(mappedBy = "companyDataBuffer")
    private Set<InvoiceBuffer> invoicesBuffer;

    public CompanyDataBuffer(String nip, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode) {
        super(nip, companyName, cityName, streetName, buildingNum, aptNum, zipCode);
        this.invoicesBuffer = new HashSet<>();
    }
    public CompanyDataBuffer(Company company){
        super(company.getNip(), company.getCompanyName(), company.getCityName(), company.getStreetName(),
                company.getBuildingNum(), company.getAptNum(), company.getZipCode());
        this.invoicesBuffer = new HashSet<>();
    }
    public void addInvoice(InvoiceBuffer buffer){
        this.invoicesBuffer.add(buffer);
    }
}
