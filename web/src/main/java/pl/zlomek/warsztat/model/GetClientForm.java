package pl.zlomek.warsztat.model;

import lombok.Getter;
import pl.zlomek.warsztat.util.Validator;

@Getter
public class GetClientForm extends AccessTokenForm{
    String username;

    public boolean validate(){
        return Validator.validateEmail(username);
    }
}
