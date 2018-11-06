package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveCarForm extends AccessTokenForm{
    long carId;

    public boolean validate(){
        return carId>0;
    }
}
