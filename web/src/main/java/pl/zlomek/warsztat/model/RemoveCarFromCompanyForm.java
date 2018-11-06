package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class RemoveCarFromCompanyForm extends AccessTokenForm{
    private long companyId;
    private long carId;

    public boolean validate(){
        return carId>0 && companyId>0;
    }
}
