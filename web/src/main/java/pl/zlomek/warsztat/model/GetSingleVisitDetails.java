package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GetSingleVisitDetails extends AccessTokenForm implements Serializable {
    private VisitDetailsResponse details;

    public GetSingleVisitDetails(String accessToken, VisitDetailsResponse details) {
        super(accessToken);
        this.details = details;
    }
}
