package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Car;
import pl.zlomek.warsztat.model.CarBrand;
import pl.zlomek.warsztat.model.CarsHasOwners;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class CarsRepository {
    @Inject
    private EntityManager em;

    public void insertCar(Car car){
        em.persist(car);
    }

    public Car updateCar(Car car){
       Car savedCar =  em.merge(car);
       return savedCar;
    }

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
            TypedQuery<Car> getCarQuery = em.createQuery("select car from Car car where id = :id",Car.class);
            getCarQuery.setParameter("id",id);
            return getCarQuery.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
