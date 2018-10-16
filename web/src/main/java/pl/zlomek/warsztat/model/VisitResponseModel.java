package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class VisitResponseModel {
    private Date visitDate;
    private String car;
    private String registrationNumber;
    public VisitResponseModel(Visit visit){
        this.visitDate =Date.from(visit.getVisitDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Car car = visit.getCar();
        this.car = new StringBuilder(car.getBrand().getBrandName()).append(" ").append(car.getModel()).toString();
        Object[] cars=  car.getOwners().stream().filter((carsHasOwners ->
             (carsHasOwners.getEndOwnershipDate() == null ||
                    (visit.getVisitDate().isBefore(carsHasOwners.getEndOwnershipDate())&&
                            visit.getVisitDate().isAfter(carsHasOwners.getBeginOwnershipDate())))
        )).limit(1).toArray();
        this.registrationNumber = ((CarsHasOwners)cars[0]).getRegistrationNumber();
    }
}
