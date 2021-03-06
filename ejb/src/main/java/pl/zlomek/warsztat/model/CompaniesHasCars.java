package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "companies_has_cars")
public class CompaniesHasCars implements Serializable{

    @EmbeddedId
    private CompaniesHasCarsId id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private CompanyOwnershipStatus status;

    @Embeddable
    @Getter
    @Setter
    private static class CompaniesHasCarsId implements Serializable{
        @Column(name = "company_pk")
        private long companyId;

        @Column(name = "car_pk")
        private long carId;

        public CompaniesHasCarsId() {
        }

        public CompaniesHasCarsId(long companyId, long carId) {
            this.companyId = companyId;
            this.carId = carId;
        }
    }

    public CompaniesHasCars(Car car, Company company) {
        this.id = new CompaniesHasCarsId(company.getId(), car.getId());
        this.car = car;
        this.company = company;
        this.status = CompanyOwnershipStatus.CURRENT_OWNER_COMPANY;
    }
}
