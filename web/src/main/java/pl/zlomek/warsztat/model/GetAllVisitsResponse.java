package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GetAllVisitsResponse extends PositiveResponse {
    private Visit[] visits;

    public GetAllVisitsResponse(String accessToken, Visit[] visits) {
        super(accessToken);
        this.visits = visits;
    }
}
