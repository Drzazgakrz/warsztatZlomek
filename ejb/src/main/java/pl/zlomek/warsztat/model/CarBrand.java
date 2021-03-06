package pl.zlomek.warsztat.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "car_brand")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CarBrand implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "brand_name")
    @Pattern(regexp = "[A-Z]{1}+[a-z]{1,}")
    private String brandName;

    public CarBrand(String name){
        this.brandName = name;
    }
}
