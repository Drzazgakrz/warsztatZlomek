package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class CompanyModel {

    @NotNull
    @Size(min = 13,max=13)
    @Column(name = "NIP")
    @Pattern(regexp = "[0-9]{3}+-+[0-9]{3}+-+[0-9]{2}+-+[0-9]{2}")
    protected String nip;

    @NotNull
    @Size(min = 2, max = 40)
    @Column(name = "company_name", unique = true)
    protected String companyName;

    @NotNull
    @Size(max = 20, min = 2)
    @Column(name = "city_name")
    @Pattern(regexp = "[A-ZŹĄĘÓŁŻ]{1}+[a-z,ąęółńćźż]{2,}")
    protected String cityName;

    @NotNull
    @Size(max = 40, min = 3)
    @Column(name = "street_name")
    @Pattern(regexp = "[A-ZŹĄĘÓŁŻ]{1}+[a-z,ąęółńćźż]{2,}")
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
    @Pattern(regexp = "[0-9]{2}+-+[0-9]{3}")
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

    public boolean compareCompanies(CompanyModel model){
        boolean result = model.getNip().equals(this.nip)&&model.getCompanyName().equals(this.companyName);
        result = result && model.getBuildingNum().equals(this.buildingNum) && model.getCityName().equals(this.cityName);
        if(this.aptNum != null)
            result = result && this.aptNum.equals(model.getAptNum());
        return result && model.getStreetName().equals(this.streetName) && model.getZipCode().equals(this.zipCode);
    }
}
