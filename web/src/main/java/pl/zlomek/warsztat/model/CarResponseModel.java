package pl.zlomek.warsztat.model;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class CarResponseModel implements Serializable {
    private long id;
    private String car;
    private String registrationNumber;

    public CarResponseModel(Car car, String registrationNumber) {
        this.car = new StringBuilder(car.getBrand().getBrandName()).append(" ").append(car.getModel()).toString();
        this.registrationNumber = registrationNumber;
        this.id = car.getId();
    }
}
