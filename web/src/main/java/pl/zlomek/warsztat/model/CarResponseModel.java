package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class CarResponseModel extends CarDataForm implements Serializable {
    private long id;

    public CarResponseModel(Car car, String registrationNumber) {
        this.id = car.getId();
        this.registrationNumber = registrationNumber;
        this.brandName = car.getBrand().getBrandName();
        this.model = car.getModel();
        this.productionYear = car.getProdYear();
        this.vin = car.getVin();
    }
}
