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
@Table(name = "invoices_Buffer")
public class InvoiceBuffer extends InvoicesModel implements Serializable {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private long id;

    @Column(name = "ststus")
    private InvoiceBufferStatus invoiceBufferStatus;


    @NotNull
    @ManyToOne
    private CompanyDataBuffer companyDataBuffer;

    @OneToMany(mappedBy = "invoiceBuffer")
    private Set<InvoiceBufferPosition> invoiceBufferPositions;

    @NotNull
    @ManyToOne
    private CarServiceData carServiceData;

    public InvoiceBuffer(int discount, MethodOfPayment methodOfPayment,
                         CompanyDataBuffer companyData, CarServiceData carServiceData, LocalDate paymentDate){
        super(discount, methodOfPayment, carServiceData, LocalDate.now(), paymentDate);
        invoiceBufferPositions = new HashSet<>();
        this.companyDataBuffer = companyData;
        this.carServiceData = carServiceData;
    }
}
