package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditCompanyForm {
    private String name;
    private String email;
    private String cityName;
    private String streetName;
    private String buildingNum;
    private String aptNum;
    private String zipCode;
    private String accessToken;
    private String currentName;
}
