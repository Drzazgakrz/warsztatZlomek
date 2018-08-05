package pl.zlomek.warsztat.model;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "car_brand")
public class CarBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String brandName;
}
