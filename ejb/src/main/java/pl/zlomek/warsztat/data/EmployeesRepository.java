package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Account;
import pl.zlomek.warsztat.model.Employee;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

public class EmployeesRepository extends AccountsRepository {

    @Transactional
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

    public Employee signIn(String username, String password){
        try {
            TypedQuery<Employee> query = em.createQuery("SELECT employee FROM Employee employee WHERE employee.email = :username AND employee.password = :password",Employee.class);
            query.setParameter("username", username);
            query.setParameter("password", Account.hashPassord(password));
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
}
