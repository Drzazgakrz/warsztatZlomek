package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Service;
import pl.zlomek.warsztat.model.VisitsHasServices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class ServicesRepository {
    @Inject
    EntityManager em;
    public Service getServiceByName(String name){
        try{
            TypedQuery<Service> query = em.createQuery("SELECT service FROM Service service WHERE service.name = :name",
                    Service.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
    public void updateService(Service service){
        em.merge(service);
    }

    public void insertVisitsServices(VisitsHasServices vhs){
        em.persist(vhs);
    }
}
