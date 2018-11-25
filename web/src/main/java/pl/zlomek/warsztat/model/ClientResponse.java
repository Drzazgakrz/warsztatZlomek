package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ClientResponse extends ClientForm{
    private String accessToken;
    private CompanyInfoResponse[] companies;
    public ClientResponse(Client client, String accessToken){
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.phoneNumber = client.getPhoneNumber();
        this.cityName = client.getCityName();
        this.streetName = client.getStreetName();
        this.buildNum = client.getBuildNum();
        this.aptNum = client.getAptNum();
        this.zipCode = client.getZipCode();
        this.accessToken = accessToken;
        List<CompaniesHasEmployees> currentCompanies = client.getCompanies().stream().filter((che) ->
            che.getStatus().equals(EmploymentStatus.CURRENT_EMPLOYER)
        ).collect(Collectors.toList());
        this.companies = new CompanyInfoResponse[currentCompanies.size()];
        int i = 0;
        for(CompaniesHasEmployees che: currentCompanies){
            this.companies[i] = new CompanyInfoResponse(null, che.getCompany());
            i++;
        }
    }
}
