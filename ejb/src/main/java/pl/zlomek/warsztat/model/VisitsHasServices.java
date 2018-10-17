package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "visit_has_services")
@NoArgsConstructor
@Getter
@Setter
public class VisitsHasServices {
    @EmbeddedId
    private VisitsHasServicesId id;

    @ManyToOne
    private Service service;

    @ManyToOne
    private Visit visit;

    private int count;


    private BigDecimal price;

    @Embeddable
    @Getter
    @Setter
    private static class VisitsHasServicesId implements Serializable {
        private long serviceId;
        private long visitId;

        public VisitsHasServicesId(long serviceId, long visitId) {
            this.serviceId = serviceId;
            this.visitId = visitId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VisitsHasServicesId that = (VisitsHasServicesId) o;
            return serviceId == that.serviceId &&
                    visitId == that.visitId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceId, visitId);
        }

        public VisitsHasServicesId() {
        }
    }

    public VisitsHasServices(Service service, Visit visit, int count, BigDecimal price) {
        this.id = new VisitsHasServicesId(service.getId(), visit.getId());
        this.service = service;
        this.visit = visit;
        this.count = count;
        this.price = price;
    }
}
