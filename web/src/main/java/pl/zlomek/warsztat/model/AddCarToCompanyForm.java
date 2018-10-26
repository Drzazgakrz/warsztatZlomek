package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class AddCarToCompanyForm extends AccessTokenForm{
    private long companyId;
    private long carId;
}
