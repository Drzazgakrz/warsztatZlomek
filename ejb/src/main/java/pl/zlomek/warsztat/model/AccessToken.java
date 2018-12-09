package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public class AccessToken {

    @Column(name = "access_token")
    protected String accessToken;

    protected LocalDateTime expiration;


    public AccessToken(String accessToken, LocalDateTime expiration) {
        this.accessToken = accessToken;
        this.expiration = expiration;
    }
}
