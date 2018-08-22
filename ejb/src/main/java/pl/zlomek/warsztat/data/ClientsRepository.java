package pl.zlomek.warsztat.data;

import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import pl.zlomek.warsztat.model.Client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@ApplicationScoped
public class ClientsRepository {
    @Inject
    EntityManager em;

    @Transactional
    public void registerUser(Client client){
        em.persist(client);
    }

    public Client findClient(String username, String password){
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
}
