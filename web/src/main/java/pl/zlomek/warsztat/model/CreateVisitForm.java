package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class CreateVisitForm extends AccessTokenForm{
    private long carId;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date visitDate;

    @JsonProperty(value="isOverview")
    private boolean isOverview;
}
