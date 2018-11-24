package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.CompanyData;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CompanyDataRespository {
    @Inject
    EntityManager em;

    @Transactional
    public void insert(CompanyData companyData){
        em.persist(companyData);
    }

    public CompanyData getCompanyDataByName(String name){
        try{
            TypedQuery<CompanyData> getCompanyData = em.createQuery("select companyData from CompanyData companyData where companyData.companyName = :name", CompanyData.class);
            getCompanyData.setParameter("name", name);
            return getCompanyData.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    public List<CompanyData> getAllCompanies(String name){
        try{
            TypedQuery<CompanyData> getCompanyData = em.createQuery("select companyData from CompanyData companyData where companyData.companyName = :name", CompanyData.class);
            getCompanyData.setParameter("name", name);
            return getCompanyData.getResultList();
        }catch (Exception e){
            return null;
        }
    }

    public void update(CompanyData data){
        em.merge(data);
    }
}
