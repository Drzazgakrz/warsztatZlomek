package pl.zlomek.warsztat.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.Serializable;
import pl.zlomek.warsztat.model.Company;

public class CompanyRepository implements Serializable {

    @Inject
    private EntityManager em;

    @Transactional
    public void insert(Company company){
        em.persist(company);
    }
}
