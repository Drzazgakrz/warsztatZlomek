package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitVisitForm {
    long visitId;
    String accessToken;
    CarPartModel[] carParts;
}
