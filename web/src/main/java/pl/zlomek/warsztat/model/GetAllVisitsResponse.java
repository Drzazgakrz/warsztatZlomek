package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GetAllVisitsResponse extends PositiveResponse implements Serializable {
    private VisitResponseModel[] visits;

    public GetAllVisitsResponse(String accessToken, VisitResponseModel
            [] visits) {
        super(accessToken);
        this.visits = visits;
    }
}
