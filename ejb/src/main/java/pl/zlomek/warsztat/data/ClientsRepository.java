package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class ClientsRepository {
    @Inject
    EntityManager em;

    @Transactional
    public void registerUser(Client client){
        em.persist(client);
    }
}
