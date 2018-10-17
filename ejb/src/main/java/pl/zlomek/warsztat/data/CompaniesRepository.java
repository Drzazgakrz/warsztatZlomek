package pl.zlomek.warsztat.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.model.CompaniesHasCars;
import pl.zlomek.warsztat.model.Company;

@ApplicationScoped
public class CompaniesRepository implements Serializable {

    @Inject
    private EntityManager em;

    private Logger log = LoggerFactory.getLogger(CompaniesRepository.class);

    public void insert(Company company){
        em.persist(company);
    }

    private void update(Company company){em.merge(company);}

    public Company getCompanyByName(String name){
        try {
            TypedQuery<Company> getCompany = em.createQuery("select companies from Company companies where companies.companyName = :name", Company.class);
            getCompany.setParameter("name", name);
            return getCompany.getSingleResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Company getCompanyById(Long id){
        try {
            TypedQuery<Company> getCompany = em.createQuery("select companies from Company companies where companies.id = :id", Company.class);
            getCompany.setParameter("id", id);
            return getCompany.getSingleResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public void updateCompany(Company company){
        em.merge(company);
    }

    public void insertCarInJoinTable(CompaniesHasCars chc){
        em.persist(chc);
    }

    public void updateJoinTable(CompaniesHasCars chc){
        em.merge(chc);
    }
}
