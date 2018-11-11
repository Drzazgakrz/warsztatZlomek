package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class ClientsCompaniesResponse extends AccessTokenForm{
    CompanyInfoResponse[] companies;

    public ClientsCompaniesResponse(String accessToken, CompanyInfoResponse[] companies) {
        super(accessToken);
        this.companies = companies;
    }
}
