package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Setter
public class CarPartModel {
    private String name;
    private BigDecimal price;
    private int count;

    public CarPartModel(String name) {
        this.name = name;
    }
}
