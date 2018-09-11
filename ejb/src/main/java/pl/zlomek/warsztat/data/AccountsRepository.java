package pl.zlomek.warsztat.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import pl.zlomek.warsztat.model.Account;
import pl.zlomek.warsztat.model.Client;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;

public abstract class AccountsRepository {

    @Inject
    protected EntityManager em;

    @Transactional
    public <Type extends Account> String generateToken(Type account){
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = JWT.create()
                .withIssuer(account.getEmail()+(new Date()).getTime())
                .sign(algorithm);
        while(this.findByToken(token)!=null){
            algorithm = Algorithm.HMAC256("secret");
            token = JWT.create()
                    .withIssuer(account.getEmail()+(new Date()).getTime())
                    .sign(algorithm);
        }
        account.setAccessToken(token);
        update(account);
        return token;
    }
    public abstract <Type extends Account> Type findByToken(String accessToken);

    public <Type extends Account> void update(Type account){
        em.merge(account);
    }
}