package pl.zlomek.warsztat.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.model.Account;
import pl.zlomek.warsztat.model.Client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;

@ApplicationScoped
public class ClientsRepository extends AccountsRepository {

    private Logger log = LoggerFactory.getLogger(ClientsRepository.class);

    @Transactional
    public void registerUser(Client client){
        em.persist(client);
    }

    public Client signIn(String username, String password){
        try {
            SHA3.DigestSHA3 sha3 = new SHA3.Digest256();
            byte[] bytes = sha3.digest(password.getBytes());
            String encrypted = Hex.toHexString(bytes);
            TypedQuery<Client> getClient = em.createQuery("select client from Client client where email = :username "
                    +"and password = :password",Client.class);
            getClient.setParameter("username", username);
            getClient.setParameter("password", encrypted);
            return getClient.getSingleResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Client findClientByUsername(String username){
        try {
            TypedQuery<Client> getClient = em.createQuery("select client from Client client where email = :username "
                    ,Client.class);
            getClient.setParameter("username", username);
            return getClient.getSingleResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Client findByToken(String token){
        try {
            TypedQuery<Client> getClient = em.createQuery("select client from Client client where accessToken "+
                            "= :token ",Client.class);
            getClient.setParameter("token", token);
            return getClient.getSingleResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
