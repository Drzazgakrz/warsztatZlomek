package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AddCoownerForm {
    private String accessToken;
    private String coownerUsername;
    private long carId;
}
