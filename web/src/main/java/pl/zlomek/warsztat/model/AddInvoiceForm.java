package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import pl.zlomek.warsztat.util.Validator;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class AddInvoiceForm extends AccessTokenForm{
    protected int discount;
    protected String methodOfPayment;
    protected String companyName;
    protected long visitId;
    @JsonFormat(pattern = "dd-MM-yyyy", shape = JsonFormat.Shape.STRING)
    protected Date paymentDate;

    public boolean validate(){
        boolean result = true;
        result = result && discount>=0;
        result = result && !methodOfPayment.equals("");
        result = result && !companyName.equals("");
        return result && visitId >0;
    }
}
