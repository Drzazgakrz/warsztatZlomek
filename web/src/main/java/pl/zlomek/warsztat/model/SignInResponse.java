package pl.zlomek.warsztat.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SignInResponse extends AccessTokenForm {
    private OverviewResponse[] overviewResponse;
    private VisitDetailsResponse[] visits;

    public SignInResponse(String accessToken,OverviewResponse[] response, VisitDetailsResponse[] visits) {
        super(accessToken);
        this.overviewResponse = response;
        this.visits = visits;
    }
}
