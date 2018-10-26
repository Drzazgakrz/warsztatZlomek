package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCarBrandForm extends AccessTokenForm{
    private String brandName;
}
