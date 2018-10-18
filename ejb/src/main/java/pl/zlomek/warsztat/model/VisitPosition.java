package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

@MappedSuperclass
@NoArgsConstructor
public abstract class VisitPosition {
    protected BigDecimal singlePrice;

    protected int count;

    public VisitPosition(BigDecimal singlePartPrice, int count) {
        this.singlePrice = singlePartPrice;
        this.count = count;
    }
}
