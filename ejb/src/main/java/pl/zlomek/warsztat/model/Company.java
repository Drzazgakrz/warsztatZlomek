package pl.zlomek.warsztat.model;



import lombok.NoArgsConstructor;
import pl.zlomek.warsztat.data.CompaniesRepository;

import javax.inject.Inject;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "companies")
public class Company extends CompanyModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 30, min = 6)
    @Pattern(regexp = "[A-Za-z0-9._-]{1,}+@+[a-z0-9]{1,6}+.+[a-z]{2,3}")
    @Column(unique = true)
    private String email;


    @OneToMany(mappedBy = "company")
    private Set<CompaniesHasEmployees> employees;

    @OneToMany(mappedBy = "company")
    private Set<CompaniesHasCars> cars;

    public Company(String nip, String email, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode){
        super(nip, companyName, cityName, streetName, buildingNum, aptNum, zipCode);
        this.email = email;
        this.employees = new HashSet<>();
        this.cars = new HashSet<>();
    }


    public CompaniesHasCars addCar(Car car){
        CompaniesHasCars companiesHasCars = new CompaniesHasCars(car, this);
        this.cars.add(companiesHasCars);
        car.getCompaniesCars().add(companiesHasCars);
        return companiesHasCars;
    }

    public CompaniesHasEmployees addClientToCompany(Client client){
        CompaniesHasEmployees companiesHasEmployees = new CompaniesHasEmployees(client, this);
        client.getCompanies().add(companiesHasEmployees);
        this.getEmployees().add(companiesHasEmployees);
        return companiesHasEmployees;
    }
}
