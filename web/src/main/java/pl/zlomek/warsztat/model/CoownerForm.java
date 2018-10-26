package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CoownerForm extends AccessTokenForm{
    private String coownerUsername;
    private long carId;
}
