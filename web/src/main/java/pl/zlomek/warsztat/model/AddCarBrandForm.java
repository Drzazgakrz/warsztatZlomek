package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

import javax.inject.Inject;

@Getter
@Setter
public class AddCarBrandForm extends AccessTokenForm{
    private String brandName;

    public boolean validate(){
        return Validator.validateNames(brandName);
    }
}
