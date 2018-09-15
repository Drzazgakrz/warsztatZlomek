package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Visit;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

public class VisitsRepository {

    @Inject
    private EntityManager em;

    @Transactional
    public void createVisit(Visit visit){
        em.persist(visit);
    }

    public void updateVisit(Visit visit){
        em.merge(visit);
    }

    public Visit getVisitById(long id){
        try {
            TypedQuery<Visit> query = em.createQuery("SELECT visit FROM Visit visit WHERE id = :id",Visit.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
