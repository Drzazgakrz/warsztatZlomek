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
    private long id;
    private Date visitDate;
    private CarResponseModel car;
    public VisitResponseModel(Visit visit){
        this.id = visit.getId();
        this.visitDate =Date.from(visit.getVisitDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Car car = visit.getCar();
        Object[] cars=  car.getOwners().stream().filter((carsHasOwners ->
                (carsHasOwners.getEndOwnershipDate() == null ||
                        (visit.getVisitDate().isBefore(carsHasOwners.getEndOwnershipDate())&&
                                visit.getVisitDate().isAfter(carsHasOwners.getBeginOwnershipDate())))
        )).limit(1).toArray();
        this.car = new CarResponseModel(car, ((CarsHasOwners)cars[0]).getRegistrationNumber());
    }
}
