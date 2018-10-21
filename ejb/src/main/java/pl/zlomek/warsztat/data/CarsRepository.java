package pl.zlomek.warsztat.data;

import org.hibernate.mapping.Collection;
import pl.zlomek.warsztat.model.Car;
import pl.zlomek.warsztat.model.CarBrand;
import pl.zlomek.warsztat.model.CarsHasOwners;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CarsRepository {
    @Inject
    private EntityManager em;

    public void insertCar(Car car){
        em.persist(car);
    }

    public Car updateCar(Car car){
       return em.merge(car);
    }

    public CarsHasOwners updateOwnership(CarsHasOwners cho){
        return em.merge(cho);
    }

    @Transactional
    public void insertOwnership(CarsHasOwners cho){
        em.persist(cho);
    }

    public CarBrand getCarBrandByName(String brandName){
        try{
            TypedQuery<CarBrand> query = em.createQuery("SELECT carBrand FROM CarBrand  carBrand WHERE brandName = :brandName", CarBrand.class);
            query.setParameter("brandName", brandName);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    public Car getCarByVin(String vin){
        try {
            TypedQuery<Car> getCarQuery = em.createQuery("select car from Car car where vin = :vin",Car.class);
            getCarQuery.setParameter("vin",vin);
            return getCarQuery.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    public Car getCarById(long id){
        try {
            String query = "select car from Car car where id = :id";
            TypedQuery<Car> getCarQuery = em.createQuery(query,Car.class);
            getCarQuery.setParameter("id",id);
            return getCarQuery.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
    public List<Car> getCarsByClientId(long clientId) {
        String query = "select car from Car car  join fetch car.owners own where own.owner.clientId = :clientIdparam";
        try {
            TypedQuery<Car> choQuery = em.createQuery(query, Car.class);
            choQuery.setParameter("clientIdparam", clientId);
            List<Car> clientCarsList = choQuery.getResultList();
            return clientCarsList;
        } catch (Exception e) {
            return null;
        }
    }

    public CarsHasOwners getOwnership(long carId, long clientId){

        try {
            TypedQuery<CarsHasOwners> choQuery = em.createQuery("select cho from CarsHasOwners cho" +
                    "  where cho.id.carId = :carId AND cho.id.ownerId = :clientId", CarsHasOwners.class);
            choQuery.setParameter("carId", carId);
            choQuery.setParameter("clientId", clientId);
            return choQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
