package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

@Getter
@Setter
@NoArgsConstructor
public class ClientCompanyForm extends AccessTokenForm{
    private String username;
    private String companyName;

    public boolean validate(){
        return Validator.validateEmail(username);
    }
}
