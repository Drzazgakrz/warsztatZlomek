package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClientResponse extends ClientForm{
    String accessToken;
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
    }
}
