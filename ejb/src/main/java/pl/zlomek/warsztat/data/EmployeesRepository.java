package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Account;
import pl.zlomek.warsztat.model.Employee;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@ApplicationScoped
public class EmployeesRepository extends AccountsRepository {

    public void registerEmployee(Employee employee){
        em.persist(employee);
    }

    @Override
    public <Type extends Account> Type findByToken(String accessToken) {
        try{
            TypedQuery<Employee> query = em.createQuery("SELECT employee FROM Employee employee WHERE accessToken = :token", Employee.class);
            query.setParameter("token",accessToken);
            return (Type) query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    public Employee signIn(String password, String username){
        try {
            TypedQuery<Employee> query = em.createQuery("SELECT employee FROM Employee employee WHERE email = :username AND password = :password", Employee.class);
            query.setParameter("password",Account.hashPassword(password));
            query.setParameter("username", username);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
