package pl.zlomek.warsztat.rest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import pl.zlomek.warsztat.data.ClientsRepository;
import pl.zlomek.warsztat.data.EmployeesRepository;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Path("/authorization")
public class Authorization {

    private Logger log = LoggerFactory.getLogger(Authorization.class);

    @Inject
    private ClientsRepository repository;

    @Inject
    private EmployeesRepository employeesRepository;

    //ścieżka localhost:8080/warsztatZlomek/rest/authorization/register
    @POST
    @Transactional
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterForm newUserData){

        if(newUserData.getPassword().equals(newUserData.getConfirmPassword())) {
            String firstName = newUserData.getFirstName();
            String lastName = newUserData.getLastName();
            String email = newUserData.getEmail();
            String phoneNum = newUserData.getPhoneNumber();
            String cityName = newUserData.getCityName();
            String streetName = newUserData.getStreetName();
            String buildNum = newUserData.getBuildNum();
            String aptNum = newUserData.getAptNum();
            String zipCode = newUserData.getZipCode();
            String password = newUserData.getPassword();
            Client client = new Client(firstName,lastName,email,phoneNum, cityName,streetName,buildNum,aptNum, zipCode,password, null);
            repository.registerUser(client);
            String token = repository.generateToken(client);
            client.setAccessToken(token);
            return Response.status(200).entity(token).build();
        } return Response.status(400).build();
    }

    @POST
    @Transactional
    @Path("/signIn")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signIn(SignInForm signInForm){
        if(signInForm.getPassword()!= null || signInForm.getUsername()!=null){
            Client client = repository.signIn(signInForm.getUsername(), signInForm.getPassword());
            if(client==null || !client.getStatus().equals(ClientStatus.ACTIVE)){
                return Response.status(401).build();
            }
            client.setLastLoggedIn(LocalDateTime.now());
            String token = repository.generateToken(client);
            return Response.ok(token).build();
        }
        return Response.status(400).build();
    }

    @POST
    @Path("/registerEmployee")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerEmployee(EmployeeRegisterForm newEmployeeData){

        if(newEmployeeData.getPassword().equals(newEmployeeData.getConfirmPassword())) {
            String firstName = newEmployeeData.getFirstName();
            String lastName = newEmployeeData.getLastName();
            String email = newEmployeeData.getEmail();
            String password = newEmployeeData.getPassword();
            LocalDate hireDate = newEmployeeData.getHireDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Employee employee = new Employee(firstName, lastName, hireDate, null, password, email, EmployeeStatus.employed);
            employeesRepository.registerEmployee(employee);
            String token = employeesRepository.generateToken(employee);
            employee.setAccessToken(token);
            return Response.status(200).entity(token).build();
        } return Response.status(400).build();
    }

    @POST
    @Path("/signInEmployee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response signInEmployee(EmployeeSignInForm form){
        if(form.getPassword() == null || form.getUsername() == null){
            return Response.status(400).build();
        }
        Employee employee = employeesRepository.signIn(form.getPassword(), form.getUsername());
        if(employee == null){
            return Response.status(401).build();
        }
        employee.setLastLoggedIn(LocalDateTime.now());
        String accessToken = employeesRepository.generateToken(employee);
        return Response.status(200).entity(accessToken).build();
    }

    @POST
    @Path("/signOut")
    @Transactional
    public Response signOut(SignOutForm form){
        Client client = repository.findByToken(form.getAccessToken());
        if(client == null){
            return Response.status(401).build();
        }
        client.setAccessToken(null);
        repository.update(client);
        return Response.status(200).build();
    }

    @POST
    @Path("/signOutEmployee")
    @Transactional
    public Response signOutEmployee(SignOutForm form){
        Employee employee = employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).build();
        }
        employee.setAccessToken(null);
        repository.update(employee);
        return Response.status(200).build();
    }

    @POST
    @Path("/banUser")
    @Transactional
    public Response banUser(BanUserForm form){
        Employee employee = employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        Client client = repository.findClientByUsername(form.getUsername());
        if(client == null){
            return Response.status(400).entity(accessToken).build();
        }
        client.setAccessToken(null);
        client.setStatus(ClientStatus.BANNED);
        repository.update(client);
        return Response.status(200).entity(accessToken).build();
    }

    @POST
    @Path("/deleteAccount")
    @Transactional
    public Response deleteAccount(RemoveUserForm form){
        Client client = repository.findByToken(form.getAccessToken());
        if(client == null || client.getStatus().equals(ClientStatus.ACTIVE)){
            return Response.status(401).build();
        }
        client.setAccessToken(null);
        client.setStatus(ClientStatus.REMOVED);
        repository.update(client);
        return Response.status(200).build();
    }

    @POST
    @Path("/removeEmployee")
    @Transactional
    public Response removeEmployee(RemoveEmployeeForm form){
        Employee employee = employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        Employee employeeToRemove = employeesRepository.findByUsername(form.getEmployeeMail());
        if(employeeToRemove == null || employee.equals(employeeToRemove)){
            return Response.status(400).entity(accessToken).build();
        }
        employee.setStatus(EmployeeStatus.quit);
        LocalDate date = form.getQuitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        employee.setQuitDate(date);
        employeesRepository.update(employee);
        return Response.status(200).entity(accessToken).build();
    }

}
