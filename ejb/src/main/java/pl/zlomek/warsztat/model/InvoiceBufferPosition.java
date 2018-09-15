package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "invoice_buffer_position")
public class InvoiceBufferPosition implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 2,max = 45)
    @Column(name = "item_name")
    private String itemName;

    @NotNull
    @Size(min=1, max=5)
    @Column(name="unit_of_measure")
    private String unitOfMeasure;

    @NotNull
    @Column(name="gross_price", precision=2 , scale =2 )
    private BigDecimal grossPrice;

    @NotNull
    @Column(name="net_price", precision=2 , scale =2 )
    private BigDecimal netPrice;

    @NotNull
    @Column(name = "VAT_tax", precision=2 , scale =2 )
    private BigDecimal vat;

    @NotNull
    @Column(name = "value_of_VAT", precision=2 , scale =2 )
    private BigDecimal valueOfVat;

    @NotNull
    @ManyToOne
    private InvoiceBuffer invoiceBuffer;

    public InvoiceBufferPosition(String itemName, String unitOfMeasure, BigDecimal grossPrice, BigDecimal netPrice, BigDecimal vat, BigDecimal valueOfVat, InvoiceBuffer invoiceBuffer) {
        this.itemName = itemName;
        this.unitOfMeasure = unitOfMeasure;
        this.grossPrice = grossPrice;
        this.netPrice = netPrice;
        this.vat = vat;
        this.valueOfVat = valueOfVat;
        this.invoiceBuffer = invoiceBuffer;
    }
}
