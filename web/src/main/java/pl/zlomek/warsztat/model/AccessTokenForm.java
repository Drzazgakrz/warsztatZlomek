package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenForm {
    protected String accessToken;

    public AccessTokenForm(String accessToken) {
        this.accessToken = accessToken;
    }
}
