package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

@Getter
@Setter
@NoArgsConstructor
public class CompanyDataForm extends AccessTokenForm{
    private String name;
    private String nip;
    private String cityName;
    private String streetName;
    private String buildingNum;
    private String aptNum;
    private String zipCode;

    public CompanyDataForm(CompanyData data) {
        this.name = data.getCompanyName();
        this.nip = data.getNip();
        this.cityName = data.getCityName();
        this.streetName = data.getStreetName();
        this.buildingNum = data.getBuildingNum();
        this.aptNum = data.getAptNum();
        this.zipCode = data.getZipCode();
    }

    public CompanyDataForm(CompanyDataBuffer data) {
        this.name = data.getCompanyName();
        this.nip = data.getNip();
        this.cityName = data.getCityName();
        this.streetName = data.getStreetName();
        this.buildingNum = data.getBuildingNum();
        this.aptNum = data.getAptNum();
        this.zipCode = data.getZipCode();
    }

    public boolean validate(){
        boolean result = Validator.validateNip(nip);
        result = result && Validator.validateNamesWithUnicode(cityName);
        result = result && Validator.validateNamesWithUnicode(streetName);
        return result && Validator.validateZipCode(zipCode);
    }
}
