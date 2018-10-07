package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCarServiceDataForm {
    private String accessToken;
    private String serviceName;
    private String nip;
    private String email;
    private String cityName;
    private String streetName;
    private String buildingNum;
    private String aptNum;
    private String zipCode;
}
