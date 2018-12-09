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
@Table(name = "invoice_position")
public class InvoicePosition implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 2,max = 45)
    @Column(name = "position_name")
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
    @Column(name = "VAT_tax")
    private int vat;

    @NotNull
    @Column(name = "value_of_VAT", precision=20 , scale =2 )
    private BigDecimal valueOfVat;

    @NotNull
    private int count;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    public InvoicePosition(String itemName, String unitOfMeasure, BigDecimal grossPrice, BigDecimal netPrice, int vat, BigDecimal valueOfVat, Invoice invoice) {
        this.itemName = itemName;
        this.unitOfMeasure = unitOfMeasure;
        this.grossPrice = grossPrice;
        this.netPrice = netPrice;
        this.vat = vat;
        this.valueOfVat = valueOfVat;
        this.invoice = invoice;
    }

    public InvoicePosition(VisitPosition position, String name, int tax, Invoice invoice, String unitOfMeasure){
        this.itemName = name;
        this.unitOfMeasure = unitOfMeasure;
        this.count = position.getCount();
        this.grossPrice = position.singlePrice.multiply(new BigDecimal(position.getCount()));
        this.invoice = invoice;
        this.vat = tax;
        double taxModifier = 100.0/(100.0+(tax*1.0));
        this.netPrice = grossPrice.multiply(new BigDecimal(taxModifier));
        this.valueOfVat = this.grossPrice.subtract(this.netPrice);
    }
    public InvoicePosition (InvoiceBufferPosition position, Invoice invoice){
        this.itemName = position.getItemName();
        this.count = position.getCount();
        this.unitOfMeasure = position.getUnitOfMeasure();
        this.grossPrice = position.getGrossPrice();
        this.netPrice = position.getNetPrice();
        this.vat = position.getVat();
        this.valueOfVat = position.getValueOfVat();
        this.invoice = invoice;
        invoice.getInvoicePositions().add(this);
    }
}
