package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class GetAllCarsResponse extends AccessTokenForm {
    private CarResponseModel[] cars;

    public GetAllCarsResponse(String accessToken, CarResponseModel[] cars) {
        super(accessToken);
        this.cars = cars;
    }
}
