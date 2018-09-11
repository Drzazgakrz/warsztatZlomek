package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;


@Getter
@Setter
public class CreateVisitForm {
    private String accessToken;
    private long carId;
    private Date VisitDate;
}
