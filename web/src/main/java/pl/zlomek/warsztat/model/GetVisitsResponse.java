package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GetVisitsResponse extends AccessTokenForm implements Serializable {
    private VisitResponseModel[] visits;

    public GetVisitsResponse(String accessToken, VisitResponseModel
            [] visits) {
        super(accessToken);
        this.visits = visits;
    }
}
