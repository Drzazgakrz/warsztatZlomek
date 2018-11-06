package pl.zlomek.warsztat.model;

import pl.zlomek.warsztat.util.Validator;

@lombok.Getter
@lombok.Setter

public class AddCarPartsForm extends AccessTokenForm{
    protected String name;
    protected int tax;
    protected String producer;

    public boolean validate(){
        boolean result = true;
        result = result && Validator.validateNamesWithUnicode(name);
        return result && Validator.validateNamesWithUnicode(producer);
    }
}
