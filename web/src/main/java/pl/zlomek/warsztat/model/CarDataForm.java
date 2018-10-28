package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDataForm extends AccessTokenForm{
    protected String vin;
    protected String registrationNumber;
    protected String model;
    protected int productionYear;
    protected String brandName;
}
