package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddInvoiceForm {
    private int discount;
    private int tax;
    private String methodOfPayment;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private BigDecimal valueOfVat;
    private String companyName;
    private String accessToken;
}
