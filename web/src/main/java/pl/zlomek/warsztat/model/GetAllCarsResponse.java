package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class GetAllCarsResponse extends PositiveResponse {
    private CarResponseModel[] cars;

    public GetAllCarsResponse(String accessToken, CarResponseModel[] cars) {
        super(accessToken);
        this.cars = cars;
    }
}
