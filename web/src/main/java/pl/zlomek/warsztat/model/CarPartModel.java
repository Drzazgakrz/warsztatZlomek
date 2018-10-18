package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarPartModel {
    private String name;
    private BigDecimal price;
    private int count;
}
