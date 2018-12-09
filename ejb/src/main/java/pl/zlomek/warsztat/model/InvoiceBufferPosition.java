package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "invoice_buffer_position")
@NoArgsConstructor
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
    @Column(name="gross_price", precision=20 , scale =2 )
    private BigDecimal grossPrice;

    @NotNull
    @Column(name="net_price", precision=20 , scale =2 )
    private BigDecimal netPrice;

    @NotNull
    @Column(name = "VAT_tax", precision=20 , scale =2 )
    private int vat;

    @NotNull
    @Column(name = "value_of_VAT", precision=20 , scale =2 )
    private BigDecimal valueOfVat;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "invoice_buffer_id")
    private InvoiceBuffer invoiceBuffer;

    @NotNull
    private int count;
    public InvoiceBufferPosition(String itemName, String unitOfMeasure, BigDecimal grossPrice, BigDecimal netPrice, int vat, BigDecimal valueOfVat, InvoiceBuffer invoiceBuffer) {
        this.itemName = itemName;
        this.unitOfMeasure = unitOfMeasure;
        this.grossPrice = grossPrice;
        this.netPrice = netPrice;
        this.vat = vat;
        this.valueOfVat = valueOfVat;
        this.invoiceBuffer = invoiceBuffer;
    }

    public InvoiceBufferPosition(VisitPosition position, String name, int tax, InvoiceBuffer invoice, String unitOfMeasure){
        this.itemName = name;
        this.unitOfMeasure = unitOfMeasure;
        this.count = position.getCount();
        this.grossPrice = position.singlePrice.multiply(new BigDecimal(position.getCount()));
        this.invoiceBuffer = invoice;
        this.vat = tax;
        double taxModifier = 100.0/(100.0+(tax*1.0));
        this.netPrice = grossPrice.multiply(new BigDecimal(taxModifier));
        this.valueOfVat = this.grossPrice.subtract(this.netPrice);
    }
}
