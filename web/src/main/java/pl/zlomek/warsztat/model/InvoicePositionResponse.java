package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class InvoicePositionResponse {
    private String positionName;
    private String grossPrice;
    private String netPrice;
    private String unitOfMeasure;
    private String valueOfVat;
    private String vatTax;

    public InvoicePositionResponse(InvoicePosition position){
        this.positionName = position.getItemName();
        this.grossPrice = position.getGrossPrice().toString();
        this.netPrice = position.getNetPrice().toString();
        this.unitOfMeasure = position.getUnitOfMeasure();
        this.valueOfVat = position.getValueOfVat().toString();
        this.vatTax = new StringBuilder(Integer.toString(position.getVat())).append("%").toString();
    }

    public InvoicePositionResponse(InvoiceBufferPosition position){
        this.positionName = position.getItemName();
        this.grossPrice = position.getGrossPrice().toString();
        this.netPrice = position.getNetPrice().toString();
        this.unitOfMeasure = position.getUnitOfMeasure();
        this.valueOfVat = position.getValueOfVat().toString();
        this.vatTax = new StringBuilder(Integer.toString(position.getVat())).append("%").toString();
    }
}
