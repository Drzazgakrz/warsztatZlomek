package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class VisitResponseModel {
    private long id;
    private Date visitDate;
    private CarResponseModel car;
    private ClientResponse[] owners;
    private ClientResponse[] notVerifiedOwners;
    private String status;

    public VisitResponseModel(Visit visit) {
        this.id = visit.getId();
        this.visitDate = Date.from(visit.getVisitDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Car car = visit.getCar();
        Object[] cars = car.getOwners().stream().filter((carsHasOwners -> {
            LocalDate end = (carsHasOwners.getEndOwnershipDate()== null)?LocalDate.now():carsHasOwners.getEndOwnershipDate();
            if(carsHasOwners.getEndOwnershipDate()== null && !visit.getVisitDate().isBefore(LocalDate.now()))
                return true;
            return end.isAfter(visit.getVisitDate()) && carsHasOwners.getBeginOwnershipDate().isBefore(visit.getVisitDate());
        }
        )).limit(1).toArray();
        Logger log = LoggerFactory.getLogger(VisitResponseModel.class);
        log.info(visitDate.toString());
        this.car = new CarResponseModel(car, ((CarsHasOwners) cars[0]).getRegistrationNumber());
        List<CarsHasOwners> owners = visit.getCar().getOwners().stream().filter(carsHasOwners ->
                carsHasOwners.getStatus().equals(OwnershipStatus.CURRENT_OWNER) || carsHasOwners.getStatus().equals(OwnershipStatus.COOWNER)).collect(Collectors.toList());
        List<CarsHasOwners> notVerified = visit.getCar().getOwners().stream().filter(carsHasOwners ->
                carsHasOwners.getStatus().equals(OwnershipStatus.NOT_VERIFIED_OWNER)).collect(Collectors.toList());
        this.owners = new ClientResponse[owners.size()];
        this.notVerifiedOwners = new ClientResponse[notVerified.size()];
        int i = 0;
        for (CarsHasOwners owner : owners) {
            this.owners[i] = new ClientResponse(owner.getOwner(), null);
        }
        i = 0;
        for (CarsHasOwners nvo : notVerified) {
            this.notVerifiedOwners[i] = new ClientResponse(nvo.getOwner(), null);
        }
        this.status = visit.getStatus().toString();
    }
}
