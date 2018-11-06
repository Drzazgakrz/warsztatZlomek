package pl.zlomek.warsztat.model;

import lombok.Getter;
import pl.zlomek.warsztat.util.Validator;

@Getter
public class GetAllCarVisitsForm {
    private String vin;

    public boolean validate(){
        return Validator.validateVin(vin);
    }
}
