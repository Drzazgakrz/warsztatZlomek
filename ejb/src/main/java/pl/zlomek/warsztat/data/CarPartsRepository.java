package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.CarPart;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CarPartsRepository {

    @Inject
    private EntityManager em;

    @Transactional
    public void saveCarPart(CarPart carPart){
        em.persist(carPart);
    }

    public void updateCarPart(CarPart carPart){
        em.merge(carPart);
    }

    public List<CarPart> getAllCarParts(){
        try{
            TypedQuery<CarPart> query = em.createQuery("SELECT carParts FROM CarPart carParts",CarPart.class);
            return query.getResultList();
        }catch (Exception e){
            return null;
        }
    }

    public CarPart getCarPartByName(String name){
        try {
            TypedQuery<CarPart> query = em.createQuery("SELECT carParts FROM CarPart carParts WHERE carParts.name = "+
                    ":carPartName",CarPart.class );
            query.setParameter("carPartName", name);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
