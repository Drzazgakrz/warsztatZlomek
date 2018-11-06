package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

@Getter
@Setter
@NoArgsConstructor
public class CarServiceDataForm extends AccessTokenForm{
    private String serviceName;
    private String nip;
    private String email;
    private String cityName;
    private String streetName;
    private String buildingNum;
    private String aptNum;
    private String zipCode;

    public CarServiceDataForm(CarServiceData data) {
        this.serviceName = data.getServiceName();
        this.nip = data.getNIP();
        this.email = data.getEmail();
        this.cityName = data.getCityName();
        this.streetName = data.getStreetName();
        this.buildingNum = data.getBuildingNum();
        this.aptNum = data.getAptNum();
        this.zipCode = data.getZipCode();
    }

    public boolean validate(){
        boolean result = !serviceName.equals("");
        result = result && Validator.validateNip(nip);
        result = result && Validator.validateEmail(email);
        result = result && Validator.validateNamesWithUnicode(cityName);
        result = result && Validator.validateNamesWithUnicode(streetName);
        return result && Validator.validateZipCode(zipCode);
    }
}
