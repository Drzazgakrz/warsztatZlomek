package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@NoArgsConstructor
@Getter
@Setter
public class VisitDetailsResponse {
    private long id;
    private VisitElementResponse[] parts;
    private VisitElementResponse[] services;
    private String visitStatus;
    private LocalDate visitDate;
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

    }
}
