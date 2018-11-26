package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyInfoResponse extends CompanyForm {
    private long id;

    public CompanyInfoResponse(String accessToken, Company company) {
        this.id = company.getId();
        this.accessToken = accessToken;
        this.name = company.getCompanyName();
        this.nip = company.getNip();
        this.email = company.getEmail();
        this.cityName = company.getCityName();
        this.streetName = company.getStreetName();
        this.buildingNum = company.getBuildingNum();
        this.aptNum = company.getBuildingNum();
        this.zipCode = company.getZipCode();
    }
}
