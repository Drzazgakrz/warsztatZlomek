package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CoownerForm {
    private String accessToken;
    private String coownerUsername;
    private long carId;
}
