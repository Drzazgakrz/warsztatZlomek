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
    private String netValue;
    private String grossValue;
    private String valueOfVat;
    private String companyName;
    private String accessToken;
}
