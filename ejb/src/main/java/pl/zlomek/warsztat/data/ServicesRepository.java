package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Service;
import pl.zlomek.warsztat.model.VisitsHasServices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

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

    public List<Service> getAllServices(){
        try{
            TypedQuery<Service> query = em.createQuery("SELECT service FROM Service service", Service.class);
            return query.getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    public void updateService(Service service){
        em.merge(service);
    }

    public void insertVisitsServices(VisitsHasServices vhs){
        em.persist(vhs);
    }

    public Service getServiceById(long id){
        try {
            TypedQuery<Service> query = em.createQuery("SELECT service FROM Service service WHERE service.id = :id", Service.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
