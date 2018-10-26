package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class BanUserForm extends AccessTokenForm{
    public BanUserForm(String accessToken) {
        super(accessToken);
    }

    String username;
}
