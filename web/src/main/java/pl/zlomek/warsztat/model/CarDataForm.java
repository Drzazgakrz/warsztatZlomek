package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDataForm {
    protected String vin;
    protected String accessToken;
    protected String registrationNumber;
    protected String model;
    protected int productionYear;
    protected String brandName;
    protected Boolean isCoowner;
}
