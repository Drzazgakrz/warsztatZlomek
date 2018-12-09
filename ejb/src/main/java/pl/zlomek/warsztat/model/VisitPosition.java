package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public abstract class VisitPosition {

    @Column(name = "single_price")
    protected BigDecimal singlePrice;

    protected int count;

    public VisitPosition(BigDecimal singlePartPrice, int count) {
        this.singlePrice = singlePartPrice;
        this.count = count;
    }
}
