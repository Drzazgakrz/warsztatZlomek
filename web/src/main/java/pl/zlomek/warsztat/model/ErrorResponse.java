package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private String error;
    private String accessToken;
    public ErrorResponse(String error, String accessToken) {
        this.error = error;
        this.accessToken = accessToken;
    }
}
