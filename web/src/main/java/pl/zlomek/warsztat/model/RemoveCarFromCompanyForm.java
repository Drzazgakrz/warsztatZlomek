package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class RemoveCarFromCompanyForm {
    private String accessToken;
    private long companyId;
    private long carId;
}
