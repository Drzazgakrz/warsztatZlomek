package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.CarBrand;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CarBrandsRespository {

    @Inject
    private EntityManager em;

    @Transactional
    public void saveCarBrand(CarBrand carBrand){
        em.persist(carBrand);
    }

    public CarBrand getCarBrandByName(String name){
        try {
            TypedQuery<CarBrand> query = em.createQuery("SELECT carBrand FROM CarBrand carBrand WHERE carBrand.brandName = :brandName", CarBrand.class);
            query.setParameter("brandName", name);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    public List<CarBrand> getAllCarBrands(){
        try {
            TypedQuery<CarBrand> query = em.createQuery("SELECT carBrand FROM CarBrand carBrand", CarBrand.class);
            return query.getResultList();
        }catch (Exception e){
            return null;
        }

    }

}
