package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "visits_has_parts")
public class VisitsParts implements Serializable {
    @Id
    @EmbeddedId
    private VisitsPartsId id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Visit visit;

    @ManyToOne(cascade = CascadeType.ALL)
    private CarPart part;

    private double singlePartPrice;

    private int count;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    private class VisitsPartsId implements Serializable{
        private long partId;
        private long visitId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VisitsPartsId that = (VisitsPartsId) o;
            return partId == that.partId &&
                    visitId == that.visitId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(partId, visitId);
        }
        public VisitsPartsId(Visit visit, CarPart carPart){

        }
    }

    public VisitsParts(Visit visit, CarPart carPart, int count, double singlePartPrice){
        this.id = new VisitsPartsId(visit, carPart);
        this.count = count;
        this.visit = visit;
        this.part = carPart;
        this.singlePartPrice = singlePartPrice;

    }
}
