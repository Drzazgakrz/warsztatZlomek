package pl.zlomek.warsztat.data;


import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ClientsRepository extends AccountsRepository {

    private Logger log = LoggerFactory.getLogger(ClientsRepository.class);

    public Client signIn(String username, String password){
        try {

            TypedQuery<Client> getClient = em.createQuery("SELECT client FROM Client client "+
                    "WHERE client.email = :username and client.password = :password",Client.class);
            getClient.setParameter("username", username);
            getClient.setParameter("password", Account.hashPassword(password));
            return getClient.getSingleResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Client findClientByUsername(String username){
        try {
            TypedQuery<Client> getClient = em.createQuery("select client from Client client "+
                            "where client.email = :username ",Client.class);
            getClient.setParameter("username", username);
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

    @Override
    public Account findByToken(String accessToken) {
        try {
            TypedQuery<ClientToken> query = em.createQuery("SELECT clientToken FROM ClientToken clientToken " +
                    "WHERE clientToken.accessToken = :accessToken", ClientToken.class);
            query.setParameter("accessToken", accessToken);
            AccessToken token =  query.getSingleResult();
            if(token != null && token.getExpiration().isAfter(LocalDateTime.now())){
                token.setExpiration(LocalDateTime.now().plusMinutes(20));
                em.merge(token);
                return ((ClientToken) token).getClient();
            }
        }catch (Exception e){
        }
        return null;
    }

    @Override
    public String generateToken(Account account) {
        String token = createToken(account);
        Client client = (Client) account;
        ClientToken clientToken = new ClientToken(token, LocalDateTime.now().plusMinutes(20), client);
        em.persist(clientToken);
        client.getAccessToken().add(clientToken);
        update(account);
        return token;
    }

    public void signOut(String accessToken){
        try {
            TypedQuery<ClientToken> query = em.createQuery("SELECT clientToken FROM ClientToken clientToken WHERE clientToken.accessToken = :accessToken", ClientToken.class);
            query.setParameter("accessToken", accessToken);
            ClientToken token = query.getSingleResult();
            token.setExpiration(LocalDateTime.now());
            em.merge(token);
        }catch (Exception e){}
    }

    public Client getClientById(long id){
        try {
            TypedQuery<Client> query = em.createQuery("SELECT client FROM Client client WHERE client.clientId = :id", Client.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
