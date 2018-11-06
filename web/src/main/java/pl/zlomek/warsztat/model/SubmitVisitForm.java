package pl.zlomek.warsztat.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitVisitForm extends AccessTokenForm{
    private long visitId;
    private CarPartModel[] carParts;
    private ServiceModel[] services;
    private Integer countYears;
    private String status;

    public boolean validate(){
        return visitId>0;
    }
}
