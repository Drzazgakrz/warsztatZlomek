package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "invoices_Buffer")
public class InvoiceBuffer extends InvoicesModel implements Serializable {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "ststus")
    private InvoiceBufferStatus invoiceBufferStatus;


    @NotNull
    @ManyToOne
    private CompanyDataBuffer companyDataBuffer;

    @NotNull
    @OneToMany(mappedBy = "invoiceBuffer")
    private Set<InvoiceBufferPosition> invoiceBufferPositions;

    @NotNull
    @ManyToOne
    private CarServiceData carServiceData;

    public InvoiceBuffer(int discount, int tax, MethodOfPayment methodOfPayment, BigDecimal netValue,
                         BigDecimal grossValue, BigDecimal valueOfVat, String invoiceNumber,
                         CompanyDataBuffer companyData, CarServiceData carServiceData) throws Exception{
        super(discount, tax, methodOfPayment, netValue, grossValue, valueOfVat);
        this.companyDataBuffer = companyData;
        this.carServiceData = carServiceData;
    }
}
