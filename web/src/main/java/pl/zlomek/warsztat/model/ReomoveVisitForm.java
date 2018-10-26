package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReomoveVisitForm extends AccessTokenForm{
    private long visitId;
}
