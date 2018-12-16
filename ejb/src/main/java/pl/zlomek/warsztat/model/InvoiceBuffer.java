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
    @JoinColumn(name = "company_data_buffer_id")
    private CompanyDataBuffer companyDataBuffer;

    @OneToMany(mappedBy = "invoiceBuffer")
    private Set<InvoiceBufferPosition> invoiceBufferPositions;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "car_service_data_id")
    private CarServiceData carServiceData;

    @OneToOne
    private Visit visit;

    public InvoiceBuffer(int discount, MethodOfPayment methodOfPayment,
                         CompanyDataBuffer companyData, CarServiceData carServiceData, LocalDate paymentDate, Visit visit){
        super(discount, methodOfPayment, carServiceData, LocalDate.now(), paymentDate);
        invoiceBufferPositions = new HashSet<>();
        this.companyDataBuffer = companyData;
        this.carServiceData = carServiceData;
        this.visit = visit;
    }
}
