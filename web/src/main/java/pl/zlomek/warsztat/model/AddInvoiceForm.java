package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddInvoiceForm {
    protected int discount;
    protected int tax;
    protected String methodOfPayment;
    protected String netValue;
    protected String grossValue;
    protected String valueOfVat;
    protected String companyName;
    protected String accessToken;
}
