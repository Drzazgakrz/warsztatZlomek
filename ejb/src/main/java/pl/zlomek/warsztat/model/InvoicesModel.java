package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.data.InvoicesRepository;

import javax.inject.Inject;
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

    protected int discount;

    @Column(name = "method_of_payment")
    protected MethodOfPayment methodOfPayment;


    //@NotNull
    @Column(name = "invoice_number")
    protected String invoiceNumber;


    public InvoicesModel(int discount, MethodOfPayment methodOfPayment) {
        this.discount = discount;
        this.methodOfPayment = methodOfPayment;
    }
}
