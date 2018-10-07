package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PositiveResponse {
    protected String accessToken;

    public PositiveResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
