package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
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

    @NotNull
    @ManyToOne
    private CarServiceData carServiceData;

    public Invoice(int discount, MethodOfPayment methodOfPayment, BigDecimal netValue, BigDecimal grossValue,
                   BigDecimal valueOfVat, CompanyData companyData, CarServiceData carServiceData) throws Exception {
        super(discount, methodOfPayment, netValue, grossValue, valueOfVat);
        this.companyData = companyData;
        this.corectionInvoice = null;
        this.carServiceData = carServiceData;
        this.invoicePositions = new HashSet<>();
        //super.invoiceNumber = super.createInvoiceNumber();
    }

    public Invoice(InvoiceBuffer buffer, CompanyModel company, CarServiceData data) throws Exception{
        super(buffer.getDiscount(), buffer.getMethodOfPayment(), buffer.getNetValue(), buffer.getGrossValue(), buffer.getValueOfVat());
        companyData = (CompanyData) company;
        carServiceData = data;
        this.invoicePositions = new HashSet<>();
        //super.invoiceNumber = super.createInvoiceNumber();
    }
}
