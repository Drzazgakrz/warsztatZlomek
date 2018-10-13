package pl.zlomek.warsztat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.model.Car;
import pl.zlomek.warsztat.model.Visit;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class VisitResponseModel {
    private LocalDate visitDate;
    private String car;
    private String registrationNumber;
    public VisitResponseModel(Visit visit){
        this.visitDate = visit.getVisitDate();
        Car car = visit.getCar();
        this.car = new StringBuilder(car.getBrand().getBrandName()).append(" ").append(car.getModel()).toString();
        this.registrationNumber = car.getRegistrationNumber();
    }
}
