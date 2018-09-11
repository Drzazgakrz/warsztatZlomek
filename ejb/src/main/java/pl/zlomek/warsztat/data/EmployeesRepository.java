package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Account;
import pl.zlomek.warsztat.model.Employee;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class EmployeesRepository extends AccountsRepository {
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
}