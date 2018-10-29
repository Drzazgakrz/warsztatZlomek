package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class GetCarDataResponse extends AccessTokenForm {
    CarResponseModel[] cars;

    public GetCarDataResponse(String accessToken, CarResponseModel[] cars) {
        super(accessToken);
        this.cars = cars;
    }
}
