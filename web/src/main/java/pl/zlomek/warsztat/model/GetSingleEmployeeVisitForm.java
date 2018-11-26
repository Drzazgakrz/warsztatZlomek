package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetSingleEmployeeVisitForm extends AccessTokenForm{
    private long visitId;

    public GetSingleEmployeeVisitForm(String accessToken, long visitId) {
        super(accessToken);
        this.visitId = visitId;
    }
}
