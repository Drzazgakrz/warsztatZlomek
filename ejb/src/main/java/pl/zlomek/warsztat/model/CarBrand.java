package pl.zlomek.warsztat.model;


import javax.persistence.*;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@Entity
@Table(name = "car_brand")
public class CarBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "brand_name")
    private String brandName;
}
