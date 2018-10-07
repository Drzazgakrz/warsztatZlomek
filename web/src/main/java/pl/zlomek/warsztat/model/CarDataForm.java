package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDataForm {
    private String vin;
    private String accessToken;
    private String registrationNumber;
    private String model;
    private int productionYear;
    private String brandName;
}
