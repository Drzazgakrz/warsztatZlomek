package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddInvoiceForm extends AccessTokenForm{
    protected int discount;
    protected String methodOfPayment;
    protected String companyName;
    protected long visitId;
}
