package pl.zlomek.warsztat.model;


import pl.zlomek.warsztat.util.Validator;

@lombok.Getter
@lombok.Setter
public class SignInForm {
    String email;
    String password;

    public boolean validate(){
        return Validator.validateEmail(email) && Validator.validatePassword(password);
    }
}
