package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import pl.zlomek.warsztat.util.Validator;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditCompanyForm extends AccessTokenForm{
    private String name;
    private String email;
    private String cityName;
    private String streetName;
    private String buildingNum;
    private String aptNum;
    private String zipCode;
    private String currentName;

    public boolean validate(){
        boolean result = Validator.validateEmail(email);
        result = result && Validator.validateNamesWithUnicode(cityName);
        result = result && Validator.validateNamesWithUnicode(streetName);
        return result && Validator.validateZipCode(zipCode);
    }
}
