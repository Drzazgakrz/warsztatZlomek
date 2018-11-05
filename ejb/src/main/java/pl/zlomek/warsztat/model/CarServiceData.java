package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "car_service_data")

public class CarServiceData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 13,max=13)
    @Pattern(regexp = "[0-9]{3}+-+[0-9]{3}+-+[0-9]{2}+-+[0-9]{2}")
    private String NIP;

    @NotNull
    @Size(max = 30, min = 6)
    @Pattern(regexp = "[A-Za-z0-9.]{1,}+@+[a-z]{1,6}+.+[a-z]{2,3}")
    private String email;

    @NotNull
    @Size(min = 2, max = 40)
    @Column(name = "service_name")
    private String serviceName;

    @NotNull
    @Size(max = 20, min = 2)
    @Column(name = "city_name")
    @Pattern(regexp = "[A-Z]{1}+[a-z]{1,}")
    private String cityName;

    @NotNull
    @Size(max = 40, min = 3)
    @Column(name = "street_name")
    @Pattern(regexp = "[A-Z]{1}+[a-z]{1,}")
    private String streetName;

    @NotNull
    @Size(max = 5, min = 1)
    @Column(name = "building_number")
    private String buildingNum;

    @Size(max = 5)
    @Column(name = "apartment_number")
    private String aptNum;

    @NotNull
    @Size(max = 6, min = 6)
    @Column(name = "zip_code")
    @Pattern(regexp = "[0-9]{2}+-+[0-9]{3}")
    private String zipCode;

    @OneToMany(mappedBy = "carServiceData")
    private Set<Invoice> invoices;

    @OneToMany(mappedBy = "carServiceData")
    private Set<InvoiceBuffer> invoicesBuffer;

    public CarServiceData(String NIP, String email, String serviceName, String cityName, String streetName, String buildingNum, String aptNum, String zipCode){
        this.NIP = NIP;
        this.email = email;
        this.serviceName = serviceName;
        this.cityName = cityName;
        this.streetName = streetName;
        this.buildingNum = buildingNum;
        this.aptNum = aptNum;
        this.zipCode = zipCode;
    }
}
