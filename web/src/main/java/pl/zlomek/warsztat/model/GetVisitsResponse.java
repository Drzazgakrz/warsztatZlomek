package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GetVisitsResponse extends AccessTokenForm implements Serializable {
    private VisitDetailsResponse[] visits;

    public GetVisitsResponse(String accessToken, VisitDetailsResponse
            [] visits) {
        super(accessToken);
        this.visits = visits;
    }
}
