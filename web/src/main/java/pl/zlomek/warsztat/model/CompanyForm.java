package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

@Getter
@Setter
@NoArgsConstructor
public class CompanyForm extends AccessTokenForm {
    protected String name;
    protected String nip;
    protected String email;
    protected String cityName;
    protected String streetName;
    protected String buildingNum;
    protected String aptNum;
    protected String zipCode;

    public boolean validate(){
        boolean result = !name.equals("");
        result = result && Validator.validateNip(nip);
        result = result && Validator.validateEmail(email);
        result = result && Validator.validateNamesWithUnicode(cityName);
        result = result && Validator.validateNamesWithUnicode(streetName);
        return result && Validator.validateZipCode(zipCode);
    }
    public CompanyForm(CarServiceData data) {
        this.name = data.getServiceName();
        this.nip = data.getNIP();
        this.email = data.getEmail();
        this.cityName = data.getCityName();
        this.streetName = data.getStreetName();
        this.buildingNum = data.getBuildingNum();
        this.aptNum = data.getAptNum();
        this.zipCode = data.getZipCode();
    }
}
