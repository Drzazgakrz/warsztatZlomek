package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "client_token")
public class ClientToken extends AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne
    private Client client;

    public ClientToken(String accessToken, LocalDateTime expiration, Client client) {
        super(accessToken, expiration);
        this.client = client;
    }
}
