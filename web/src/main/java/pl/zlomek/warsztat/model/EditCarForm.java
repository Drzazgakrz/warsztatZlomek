package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditCarForm extends CarDataForm{
    private long carId;
}
