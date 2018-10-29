package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class VerificationForm extends AccessTokenForm{
    private ClientResponse[] owners;
    private ClientResponse[] notOwners;
    private CarResponseModel car;
}
