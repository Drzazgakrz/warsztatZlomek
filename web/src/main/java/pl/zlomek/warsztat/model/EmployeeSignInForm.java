package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

@Getter
@Setter
public class EmployeeSignInForm {
    String username;
    String password;

    public boolean validate(){
        return Validator.validateEmail(username) && Validator.validatePassword(password);
    }
}
