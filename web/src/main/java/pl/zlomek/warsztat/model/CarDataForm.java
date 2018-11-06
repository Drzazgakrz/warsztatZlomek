package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

import java.time.LocalDate;

@Getter
@Setter
public class CarDataForm extends AccessTokenForm{
    protected String vin;
    protected String registrationNumber;
    protected String model;
    protected int productionYear;
    protected String brandName;

    public boolean validate(){
        boolean result = true;
        result = result && Validator.validateVin(vin);
        result = result && Validator.validateRegistrationNumber(registrationNumber);
        result = result && Validator.validateNames(model);
        result = result && (productionYear >1930) && (productionYear < LocalDate.now().getYear());
        return result && Validator.validateNames(brandName);
    }
}
