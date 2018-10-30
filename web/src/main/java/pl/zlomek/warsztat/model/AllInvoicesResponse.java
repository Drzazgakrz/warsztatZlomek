package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class AllInvoicesResponse extends AccessTokenForm{
    private InvoiceResponse[] invoices;

    public AllInvoicesResponse(String accessToken, InvoiceResponse[] invoices) {
        super(accessToken);
        this.invoices = invoices;
    }
}
