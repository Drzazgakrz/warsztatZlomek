package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.zlomek.warsztat.util.Validator;

@NoArgsConstructor
@Getter
public class CoownerForm extends AccessTokenForm{
    private String coownerUsername;
    private long carId;

    public boolean validate(){
        return Validator.validateEmail(coownerUsername) && carId>0;
    }
}
