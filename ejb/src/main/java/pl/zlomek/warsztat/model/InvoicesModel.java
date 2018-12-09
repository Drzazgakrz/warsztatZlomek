package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zlomek.warsztat.data.InvoicesRepository;

import javax.inject.Inject;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    @NotNull
    @ManyToOne
    @JoinColumn(name = "car_service_data_id")
    protected CarServiceData carServiceData;

    @NotNull
    @Column(name = "day_of_issue")
    protected LocalDate dayOfIssue;

    @NotNull
    @Column(name = "payment_date")
    protected LocalDate paymentDate;

    @Column(precision = 20, scale = 2, name = "net_value")
    protected BigDecimal netValue;

    @Column(precision = 20, scale = 2, name = "gross_value")
    protected BigDecimal grossValue;

    public InvoicesModel(int discount, MethodOfPayment methodOfPayment, CarServiceData carServiceData,
                         LocalDate dayOfIssue, LocalDate paymentDate) {
        this.discount = discount;
        this.methodOfPayment = methodOfPayment;
        this.carServiceData = carServiceData;
        this.dayOfIssue = dayOfIssue;
        this.paymentDate = paymentDate;
    }
}
