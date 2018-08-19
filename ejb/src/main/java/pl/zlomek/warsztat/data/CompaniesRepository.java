package pl.zlomek.warsztat.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import pl.zlomek.warsztat.model.Company;

public class CompaniesRepository implements Serializable {

    @Inject
    private EntityManager em;

    @Transactional
    public void insert(Company company){
        em.persist(company);
    }

    public Company getCompanyByName(String name){
        try {
            TypedQuery<Company> getCompany = em.createQuery("select companies from companies companies where companies.company_name = :name", Company.class);
            getCompany.setParameter("name", name);
            return getCompany.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
