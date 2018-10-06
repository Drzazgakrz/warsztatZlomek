package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GetAllVisitsResponse {
    private String accessToken;
    private Visit[] visits;

    public GetAllVisitsResponse(String accessToken, Visit[] visits) {
        this.accessToken = accessToken;
        this.visits = visits;
    }
}
