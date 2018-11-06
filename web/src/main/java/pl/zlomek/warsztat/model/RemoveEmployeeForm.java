package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import pl.zlomek.warsztat.util.Validator;

import java.util.Date;

@Getter
public class RemoveEmployeeForm extends AccessTokenForm{
    private String employeeMail;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date quitDate;

    public boolean validate(){
        return Validator.validateEmail(employeeMail);
    }
}
