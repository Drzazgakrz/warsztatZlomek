package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class GetCarDataResponse extends AccessTokenForm {
    CarResponseModel car;

    public GetCarDataResponse(String accessToken, Car car, String registration) {
        super(accessToken);
        this.car = new CarResponseModel(car, registration);
    }
}
