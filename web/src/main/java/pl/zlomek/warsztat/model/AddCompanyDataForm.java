package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCompanyDataForm {
    private String accessToken;
    private String name;
    private String nip;
    private String cityName;
    private String streetName;
    private String buildingNum;
    private String aptNum;
    private String zipCode;
}
