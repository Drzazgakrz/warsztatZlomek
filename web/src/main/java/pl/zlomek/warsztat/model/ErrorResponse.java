package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse extends AccessTokenForm{
    private String error;
    public ErrorResponse(String error, String accessToken) {
        this.error = error;
        this.accessToken = accessToken;
    }
}
