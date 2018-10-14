package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Date;

@Getter
public class RemoveEmployeeForm {
    private String accessToken;
    private String employeeMail;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date quitDate;
}