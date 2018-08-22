package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Car;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class CarRepository {
    @Inject
    EntityManager em;

    public void insertCar(Car car){
        em.persist(car);
    }

    public Car getCar(String vin){
        try {
            TypedQuery<Car> getCarQuery = em.createQuery("select car from Car car where vin = :vin"
                    ,Car.class);
            getCarQuery.setParameter("vin",vin);
            return getCarQuery.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
