package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class InvoiceDetailsResponse extends AccessTokenForm {
    private InvoiceResponse invoice;

    public InvoiceDetailsResponse(String accessToken, Invoice invoice){
        this.invoice = new InvoiceResponse(invoice);
        this.accessToken = accessToken;
    }
    public InvoiceDetailsResponse(String accessToken, InvoiceBuffer invoice){
        this.invoice = new InvoiceResponse(invoice);
        this.accessToken = accessToken;
    }
}
