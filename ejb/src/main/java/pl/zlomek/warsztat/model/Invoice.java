package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "invoices")
public class Invoice extends InvoicesModel implements Serializable {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private CompanyData companyData;

    //@NotNull
    @OneToMany (mappedBy = "invoice")
    private Set<InvoicePosition> invoicePositions;

    @OneToOne
    private Invoice corectionInvoice;

    @Column(name = "visit_finished")
    private LocalDate visitFinished;

    
    public Invoice(int discount, MethodOfPayment methodOfPayment,CompanyData companyData, CarServiceData carServiceData, LocalDate paymentDate){
        super(discount, methodOfPayment, carServiceData, LocalDate.now(), paymentDate );
        this.companyData = companyData;
        this.corectionInvoice = null;
        this.carServiceData = carServiceData;
        this.invoicePositions = new HashSet<>();
        this.dayOfIssue = LocalDate.now();
    }

    public Invoice(InvoiceBuffer buffer, CompanyModel company, CarServiceData data){
        super(buffer.getDiscount(), buffer.getMethodOfPayment(), data, LocalDate.now(), buffer.getPaymentDate());
        this.netValue = buffer.getNetValue();
        this.grossValue = buffer.getGrossValue();
        companyData = (CompanyData) company;
        this.invoicePositions = new HashSet<>();

    }
}
