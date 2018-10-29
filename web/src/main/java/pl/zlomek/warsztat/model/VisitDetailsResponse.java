package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class VisitDetailsResponse {
    private long id;
    private VisitElementResponse[] parts;
    private VisitElementResponse[] services;
    private String visitStatus;
    private LocalDate visitDate;
    private ClientResponse[] owners;
    private ClientResponse[] notVerifiedOwners;
    public VisitDetailsResponse(Visit visit){
        this.id = visit.getId();
        this.parts = new VisitElementResponse[visit.getParts().size()];
        int i = 0;
        for(VisitsParts part : visit.getParts()){
            parts[i] = new VisitElementResponse(part.getPart().getName(), part.getSinglePrice().toString(), part.getCount());
            i++;
        }
        this.services = new VisitElementResponse[visit.getParts().size()];
        i = 0;
        for(VisitsHasServices service : visit.getServices()){
            services[i] = new VisitElementResponse(service.getService().getName(), service.getSinglePrice().toString(), service
                    .getCount());
            i++;
        }
        this.visitStatus = visit.getStatus().toString();
        this.visitDate = visit.getVisitDate();
        List<CarsHasOwners> owners = visit.getCar().getOwners().stream().filter(carsHasOwners ->
                carsHasOwners.getStatus().equals(OwnershipStatus.CURRENT_OWNER)|| carsHasOwners.getStatus().equals(OwnershipStatus.COOWNER)).collect(Collectors.toList());
        List<CarsHasOwners> notVerified = visit.getCar().getOwners().stream().filter(carsHasOwners ->
                carsHasOwners.getStatus().equals(OwnershipStatus.NOT_VERIFIED_OWNER)).collect(Collectors.toList());
        this.owners = new ClientResponse[owners.size()];
        this.notVerifiedOwners = new ClientResponse[notVerified.size()];
        i = 0;
        for(CarsHasOwners owner: owners){
            this.owners[i] = new ClientResponse(owner.getOwner(), null);
        }
        i = 0;
        for(CarsHasOwners nvo: notVerified){
            this.notVerifiedOwners[i] = new ClientResponse(nvo.getOwner(), null);
        }
    }
}
