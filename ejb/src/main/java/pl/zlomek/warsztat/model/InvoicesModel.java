package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.data.InvoicesRepository;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.GregorianCalendar;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class InvoicesModel {


    @Transient
    private InvoicesRepository repository = new InvoicesRepository();

    protected int discount;

    @NotNull
    protected int tax;

    @NotNull
    @Column(name = "method_of_payment")
    protected MethodOfPayment methodOfPayment;

    @NotNull
    @Column(name = "net_value", precision = 2, scale = 2)
    protected BigDecimal netValue;

    @NotNull
    @Column(name = "gross_value", precision = 2, scale = 2)
    protected BigDecimal grossValue;

    @NotNull
    @Column(name = "value_of_VAT", precision = 2, scale = 2)
    protected BigDecimal valueOfVat;

    @NotNull
    @Column(name = "invoice_number")
    protected String invoiceNumber;

    public String createInvoiceNumber() throws Exception {
        int number = repository.countInvoicesInMonth();
        if (number == -1)
            throw new Exception();
        StringBuilder invoiceNumberBuilder = new StringBuilder(number).append("/").append(GregorianCalendar.MONTH);
        return invoiceNumberBuilder.append("/").append(GregorianCalendar.YEAR).toString();
    }

    public InvoicesModel(int discount, int tax, MethodOfPayment methodOfPayment, BigDecimal netValue, BigDecimal grossValue, BigDecimal valueOfVat) {
        this.discount = discount;
        this.tax = tax;
        this.methodOfPayment = methodOfPayment;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.valueOfVat = valueOfVat;
    }
}
