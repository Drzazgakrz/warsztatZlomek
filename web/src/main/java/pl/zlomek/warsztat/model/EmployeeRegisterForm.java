package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import pl.zlomek.warsztat.util.Validator;

import java.util.Date;

@lombok.Getter
@lombok.Setter
public class EmployeeRegisterForm extends AccessTokenForm{
    private String firstName;
    private String lastName;
    private String email;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date hireDate;
    private String password;
    private String confirmPassword;

    public boolean validate(){
        boolean result = Validator.validateNamesWithUnicode(firstName) && Validator.validateNamesWithUnicode(lastName);
        result = result && Validator.validateEmail(email);
        return result && Validator.validatePassword(password) && Validator.validatePassword(confirmPassword);
    }
}
