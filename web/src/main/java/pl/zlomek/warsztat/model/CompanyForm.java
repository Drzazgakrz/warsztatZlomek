package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyForm extends AccessTokenForm {
    protected String name;
    protected String nip;
    protected String email;
    protected String cityName;
    protected String streetName;
    protected String buildingNum;
    protected String aptNum;
    protected String zipCode;
}
