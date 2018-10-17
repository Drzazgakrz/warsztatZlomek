package pl.zlomek.warsztat.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitVisitForm {
    private long visitId;
    private String accessToken;
    private CarPartModel[] carParts;
    private ServiceModel[] services;
    private Integer countYears;
}
