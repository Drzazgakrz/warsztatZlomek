package pl.zlomek.warsztat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.model.Car;
import pl.zlomek.warsztat.model.CarsHasOwners;
import pl.zlomek.warsztat.model.Visit;

import java.time.LocalDate;
import java.util.stream.Stream;

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
        Object[] cars=  car.getOwners().stream().filter((carsHasOwners ->
             (carsHasOwners.getEndOwnershipDate() == null ||
                    (this.visitDate.isBefore(carsHasOwners.getEndOwnershipDate())&&
                            this.visitDate.isAfter(carsHasOwners.getBeginOwnershipDate())))
        )).limit(1).toArray();
        this.registrationNumber = ((CarsHasOwners)cars[0]).getRegistrationNumber();
    }
}
