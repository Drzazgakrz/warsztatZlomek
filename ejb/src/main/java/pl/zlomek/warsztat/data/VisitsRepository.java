package pl.zlomek.warsztat.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitsRepository {

    private Logger log = LoggerFactory.getLogger(VisitsRepository.class);

    @Inject
    private EntityManager em;

    @Inject
    ClientsRepository repository;

    public void createVisit(Visit visit) {
        em.persist(visit);
    }

    public void removeVisit(Visit visit) {
        em.remove(visit);
    }

    public void createVisitPart(VisitsParts parts) {
        em.persist(parts);
    }

    public void updateVisit(Visit visit) {
        visit.setUpdatedAt(LocalDateTime.now());
        em.merge(visit);
    }

    public void createOverview(Overview overview) {
        em.persist(overview);
    }

    public Visit getVisitById(long id) {
        try {
            TypedQuery<Visit> query = em.createQuery("SELECT visit FROM Visit visit WHERE id = :id", Visit.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Visit> getCarVisit(CarsHasOwners cho) {
        try {
            LocalDate endDate = (cho.getEndOwnershipDate() == null) ? LocalDate.now() : cho.getEndOwnershipDate();
            LocalDate beginDate = cho.getBeginOwnershipDate();
            TypedQuery<Visit> query = em.createQuery("SELECT visits FROM Visit visits JOIN FETCH visits.car car" +
                    " JOIN FETCH visits.employee employee JOIN FETCH visits.overview overview JOIN FETCH visits.parts parts " +
                    "JOIN FETCH visits.services services WHERE visits.visitDate>=:beginDate AND visits.visitDate<=:endDate AND "+
                    "visits.car = :car", Visit.class);
            query.setParameter("beginDate", beginDate);
            query.setParameter("endDate", endDate);
            query.setParameter("car",cho.getCar());
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Visit> getAllVisits(){
        try {
            TypedQuery<Visit> query = em.createQuery("SELECT visits FROM Visit visits ORDER BY visits.visitDate DESC", Visit.class);
            return query.getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    public void insertService(Service service){
        em.persist(service);
    }

    public List<Visit> getAllNewVisits(){
        try {
            TypedQuery<Visit> query = em.createQuery("SELECT visits FROM Visit visits WHERE visits.status = :status ORDER BY visits.visitDate", Visit.class);
            query.setParameter("status",VisitStatus.NEW);
            return query.getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
}
