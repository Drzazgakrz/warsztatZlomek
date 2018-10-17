package pl.zlomek.warsztat.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import pl.zlomek.warsztat.model.AccessToken;
import pl.zlomek.warsztat.model.Account;
import pl.zlomek.warsztat.model.Client;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;

public abstract class AccountsRepository  <Type extends Account> {

    @Inject
    protected EntityManager em;

    public abstract  String generateToken(Type account);

    public abstract Account findByToken(String accessToken);

    public void update(Type account) {
        em.merge(account);
    }

    public void insert(Type account) {
        em.persist(account);
    }

    protected String createToken(Type user){
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = JWT.create()
                .withIssuer(user.getEmail() + (new Date()).getTime())
                .sign(algorithm);
        while (this.findByToken(token) != null) {
            algorithm = Algorithm.HMAC256("secret");
            token = JWT.create()
                    .withIssuer(user.getEmail() + (new Date()).getTime())
                    .sign(algorithm);
        }
        return token;
    }
}

