package pl.zlomek.warsztat.model;

import pl.zlomek.warsztat.util.Validator;

@lombok.Getter
public class ClientForm {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
    protected String cityName;
    protected String streetName;
    protected String buildNum;
    protected String aptNum;
    protected String zipCode;
    protected String password;
    protected String confirmPassword;

    public boolean validate(){
        boolean result = Validator.validateNamesWithUnicode(firstName) && Validator.validateNamesWithUnicode(lastName);
        result = result && Validator.validateEmail(email);
        result = result && Validator.validateNamesWithUnicode(cityName);
        result = result && Validator.validateNamesWithUnicode(streetName);
        result = result && Validator.validateZipCode(zipCode);
        return result && Validator.validatePassword(password) && Validator.validatePassword(confirmPassword);
    }
}
