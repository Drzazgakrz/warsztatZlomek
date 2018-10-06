package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.CarServiceData;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;

@ApplicationScoped
public class CarServiceDataRespository implements Serializable {

    @Inject
    private EntityManager em;

    @Transactional
    public void insert(CarServiceData carServiceData){
        em.persist(carServiceData);
    }

    public CarServiceData getTopServiceData()
    {
        try{
            TypedQuery<CarServiceData> getDataQuery = em.createQuery("select carServiceData from CarServiceData carServiceData where id = (select max (carServiceData.id) from CarServiceData carServiceData)", CarServiceData.class);
            return getDataQuery.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
