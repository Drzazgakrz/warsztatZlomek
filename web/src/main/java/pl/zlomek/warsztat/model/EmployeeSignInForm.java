package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

@Getter
@Setter
public class EmployeeSignInForm {
    String email;
    String password;

    public boolean validate(){
        return Validator.validateEmail(email) && Validator.validatePassword(password);
    }
}
