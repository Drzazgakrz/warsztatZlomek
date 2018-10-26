package pl.zlomek.warsztat.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SignInResponse extends AccessTokenForm {
    private OverviewResponse[] overviewResponse;
    private VisitResponseModel[] visits;

    public SignInResponse(String accessToken,OverviewResponse[] response, VisitResponseModel[] visits) {
        super(accessToken);
        this.overviewResponse = response;
        this.visits = visits;
    }
}
