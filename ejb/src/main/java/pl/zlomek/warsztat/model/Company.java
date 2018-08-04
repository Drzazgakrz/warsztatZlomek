package pl.zlomek.warsztat.model;


import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

//Tabela
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@Entity
@Table(name = "comapnies")
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String NIP;

    @NotNull
    private String companyName;

    @NotNull
    private String cityName;

    @NotNull
    private String streetName;

    @NotNull
    private String buildingNum;

    private String aptNum;

    @NotNull
    private String zipCode;

    public Company(String NIP, String companyName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode){
        this.NIP = NIP;
        this.companyName = companyName;
        this.cityName = cityName;
        this.streetName = streetName;
        this.buildingNum = buildingNum;
        this.aptNum = aptNum;
        this.zipCode = zipCode;
    }
}
