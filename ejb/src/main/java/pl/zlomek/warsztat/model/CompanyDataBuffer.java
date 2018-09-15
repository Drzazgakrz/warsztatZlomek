package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "company_data_buffer")

public class CompanyDataBuffer implements Serializable {
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

    @NotNull
    @OneToMany(mappedBy = "companyDataBuffer")
    private Set<InvoiceBuffer> invoicesBuffer;

    public CompanyDataBuffer(String nip, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode) {
        this.nip = nip;
        this.companyName = companyName;
        this.cityName = cityName;
        this.streetName = streetName;
        this.buildingNum = buildingNum;
        this.aptNum = aptNum;
        this.zipCode = zipCode;
        this.invoicesBuffer = invoicesBuffer;
    }
}
