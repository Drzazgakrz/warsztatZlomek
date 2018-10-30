package pl.zlomek.warsztat.model;

import lombok.Getter;

import java.time.ZoneId;
import java.util.Date;

@Getter
public class InvoiceResponse {
    private Date dayOfIssue;
    private int discount;
    private String grossValue;
    private String netValue;
    private String invoiceNumber;
    private String methodOfPayment;
    private Date paymentDate;
    private CarServiceDataForm carServiceData;
    private CompanyDataForm companyData;
    private InvoicePositionResponse[] positions;

    public InvoiceResponse(Invoice invoice){
        this.dayOfIssue = Date.from(invoice.getDayOfIssue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        this.discount = invoice.getDiscount();
        this.netValue = invoice.getNetValue().toString();
        this.grossValue = invoice.getGrossValue().toString();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.methodOfPayment = invoice.getMethodOfPayment().toString();
        this.paymentDate = Date.from(invoice.getPaymentDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        this.carServiceData = new CarServiceDataForm(invoice.getCarServiceData());
        this.companyData = new CompanyDataForm(invoice.getCompanyData());
        this.positions = new InvoicePositionResponse[invoice.getInvoicePositions().size()];
        int i = 0;
        for(InvoicePosition position : invoice.getInvoicePositions()){
            this.positions[i] = new InvoicePositionResponse(position);
            i++;
        }

    }

    public InvoiceResponse(InvoiceBuffer invoice){
        this.dayOfIssue = Date.from(invoice.getDayOfIssue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        this.discount = invoice.getDiscount();
        this.netValue = invoice.getNetValue().toString();
        this.grossValue = invoice.getGrossValue().toString();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.methodOfPayment = invoice.getMethodOfPayment().toString();
        this.paymentDate = Date.from(invoice.getPaymentDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        this.carServiceData = new CarServiceDataForm(invoice.getCarServiceData());
        this.companyData = new CompanyDataForm(invoice.getCompanyDataBuffer());
        this.positions = new InvoicePositionResponse[invoice.getInvoiceBufferPositions().size()];
        int i = 0;
        for(InvoiceBufferPosition position : invoice.getInvoiceBufferPositions()){
            this.positions[i] = new InvoicePositionResponse(position);
            i++;
        }

    }
}
