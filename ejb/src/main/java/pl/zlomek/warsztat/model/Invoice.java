package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "invoices")
public class Invoice implements Serializable {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private long id;

    @NotNull //???
    @Size(min=1, max=2)
    private int discount;

    @NotNull
    @Size(min=2, max=2)
    private  int tax;

    @NotNull
    @Column(name = "method_of_payment")
    private MethodOfPayment methodOfPayment;

    @NotNull
    @Column(name = "net_value", precision=2 , scale =2 )
    private BigDecimal netValue;

    @NotNull
    @Column(name = "gross_value", precision=2 , scale =2 )
    private  BigDecimal grossValue;

    @NotNull
    @Column(name = "value_of_VAT", precision=2 , scale =2 )
    private BigDecimal valueOfVat;

    @NotNull
    @Column(name = "invoice_number")
    private String invoiceNumber;

    @ManyToOne
    @NotNull
    private CompanyData companyData;

    @NotNull
    @OneToMany (mappedBy = "invoice")
    private Set<InvoicePosition> invoicePositions;

    @OneToOne
    private Invoice corectionInvoice;
    ///jeszcze sama do siebie i konstruktora i rlacji z dane  serwisu, jeszcze itemów


}
