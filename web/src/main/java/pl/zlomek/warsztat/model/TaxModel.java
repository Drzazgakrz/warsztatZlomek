package pl.zlomek.warsztat.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TaxModel {
    private BigDecimal netValue;
    private BigDecimal grossValue;

    public TaxModel(BigDecimal netValue, BigDecimal grossValue) {
        this.netValue = netValue;
        this.grossValue = grossValue;
    }
}
