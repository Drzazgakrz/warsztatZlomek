package pl.zlomek.warsztat.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import pl.zlomek.warsztat.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class EmployeesRepository extends AccountsRepository {

    public Employee signIn(String password, String username){
        try {
            TypedQuery<Employee> query = em.createQuery("SELECT employee FROM Employee employee WHERE" +
                    " employee.email = :username AND employee.password = :password", Employee.class);
            query.setParameter("password",Account.hashPassword(password));
            query.setParameter("username", username);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    public Employee findByUsername(String username){
        try{
            TypedQuery<Employee> query = em.createQuery("SELECT employee FROM Employee employee WHERE " +
                    "employee.email = :username",Employee.class);
            query.setParameter("username",username);
            return query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String generateToken(Account account) {
        String token = createToken(account);
        Employee employee = (Employee) account;
        EmployeeToken employeeToken = new EmployeeToken(token, LocalDateTime.now().plusMinutes(20), employee);
        em.persist(employeeToken);
        employee.getAccessToken().add(employeeToken);
        update(account);
        return token;
    }

    @Override
    public Account findByToken(String accessToken) {
        try {
            TypedQuery<EmployeeToken> query = em.createQuery("SELECT employeeToken FROM EmployeeToken employeeToken " +
                    "WHERE employeeToken.accessToken = :accessToken", EmployeeToken.class);
            query.setParameter("accessToken", accessToken);
            AccessToken token =  query.getSingleResult();
            if(token != null && token.getExpiration().isAfter(LocalDateTime.now())){
                token.setExpiration(LocalDateTime.now().plusMinutes(20));
                return ((EmployeeToken) token).getEmployee();
            }
        }catch (Exception e){
        }
        return null;
    }

    public List<Visit> getAllEmployeeVisits(Employee employee){
        try{
            TypedQuery<Visit> query = em.createQuery("SELECT visit FROM Visit visit WHERE visit.employee = :employee", Visit.class);
            query.setParameter("employee", employee);
            return query.getResultList();
        }catch (Exception e){
            return null;
        }
    }
}
