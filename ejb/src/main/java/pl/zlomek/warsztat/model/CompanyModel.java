package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class CompanyModel {

    @NotNull
    @Size(min = 14,max=14)
    @Column(name = "NIP")
    protected String nip;

    @NotNull
    @Size(min = 2, max = 40)
    @Column(name = "company_name")
    protected String companyName;

    @NotNull
    @Size(max = 20, min = 2)
    @Column(name = "city_name")
    protected String cityName;

    @NotNull
    @Size(max = 40, min = 3)
    @Column(name = "street_name")
    protected String streetName;

    @NotNull
    @Size(max = 5, min = 1)
    @Column(name = "building_number")
    protected String buildingNum;

    @Size(max = 5)
    @Column(name = "apartment_number")
    protected String aptNum;

    @NotNull
    @Size(max = 6, min = 6)
    @Column(name = "zip_code")
    protected String zipCode;

    public CompanyModel(String nip, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode) {
        this.nip = nip;
        this.companyName = companyName;
        this.cityName = cityName;
        this.streetName = streetName;
        this.buildingNum = buildingNum;
        this.aptNum = aptNum;
        this.zipCode = zipCode;
    }
}