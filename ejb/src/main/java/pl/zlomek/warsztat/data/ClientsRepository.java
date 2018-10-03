package pl.zlomek.warsztat.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.model.Account;
import pl.zlomek.warsztat.model.CarsHasOwners;
import pl.zlomek.warsztat.model.Client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ClientsRepository extends AccountsRepository {

    private Logger log = LoggerFactory.getLogger(ClientsRepository.class);

    public void registerUser(Client client){
        em.persist(client);
    }

    public Client signIn(String username, String password){
        try {

            TypedQuery<Client> getClient = em.createQuery("SELECT client FROM Client client "+
                    "WHERE client.email = :username and client.password = :password",Client.class);
            getClient.setParameter("username", username);
            getClient.setParameter("password", super.hashPassword(password));
            return getClient.getSingleResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Client findClientByUsername(String username){
        try {
            TypedQuery<Client> getClient = em.createQuery("select cars from Client client "+
                            "where client.email = :username ",Client.class);
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
    public List<CarsHasOwners> getClientsCar(Client client){
        try {
            TypedQuery<CarsHasOwners> query = em.createQuery("SELECT cho FROM  CarsHasOwners cho WHERE cho.owner = :client", CarsHasOwners.class);
            query.setParameter("client", client);
            return query.getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }

    }
}
