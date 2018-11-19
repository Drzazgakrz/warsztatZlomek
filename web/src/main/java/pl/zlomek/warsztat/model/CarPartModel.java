package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarPartModel {
    private String name;
    private BigDecimal price;
    private int count;
    private long id;
    private String producer;
    private int tax;

    public CarPartModel(CarPart carPart) {
        this.name = carPart.getName();
        this.id = carPart.getId();
        this.producer = carPart.getProducer();
        this.tax = carPart.getTax();
    }
}
