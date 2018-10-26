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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterForm newUserData) {

        if (newUserData.getPassword().equals(newUserData.getConfirmPassword())) {
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
            Client client = new Client(firstName, lastName, email, phoneNum, cityName, streetName, buildNum, aptNum, zipCode, password, null);
            repository.insert(client);
            String token = repository.generateToken(client);
            return Response.status(200).entity(new AccessTokenForm(token)).build();
        }
        return Response.status(400).entity(new ErrorResponse("Brak kompletnych danych logowania", null)).build();
    }

    @POST
    @Transactional
    @Path("/signIn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(SignInForm signInForm) {
        if (signInForm.getPassword() != null || signInForm.getUsername() != null) {
            Client client = repository.signIn(signInForm.getUsername(), signInForm.getPassword());
            if (client == null || !client.getStatus().equals(ClientStatus.ACTIVE)) {
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            }
            List<Overview> overviews = getOverviews(client, LocalDate.now().plusMonths(1));
            OverviewResponse[] overviewsArray = new OverviewResponse[overviews.size()];
            int i = 0;
            for(Overview overview : overviews){
                overviewsArray[i] = new OverviewResponse(overview);
                i++;
            }

            List<Visit> visits = getVisits(client, LocalDate.now().plusDays(7));
            VisitResponseModel[] visitsArray = new VisitResponseModel[visits.size()];
            i = 0;
            for(Visit visit : visits){
                visitsArray[i] = new VisitResponseModel(visit);
                i++;
            }

            client.setLastLoggedIn(LocalDateTime.now());
            String token = repository.generateToken(client);
            return Response.status(200).entity(new SignInResponse(token, overviewsArray,visitsArray)).build();
        }
        return Response.status(400).entity(new ErrorResponse("Brak kompletnych danych logowania", null)).build();
    }

    public List<Overview> getOverviews(Client client, LocalDate date) {
        Object[] currentClientCars = client.getCars().stream().filter(carsHasOwners -> {
            OwnershipStatus currentStatus = carsHasOwners.getStatus();
            return (currentStatus.equals(OwnershipStatus.CURRENT_OWNER) || currentStatus.equals(OwnershipStatus.COOWNER));
        }).toArray();
        List<Overview> overviews = new ArrayList<>();
        for (Object currentOwnership : currentClientCars) {
            Car car = ((CarsHasOwners) currentOwnership).getCar();
            overviews.addAll(car.getOverviews().stream().filter(overview -> {
                if(date != null){
                    return overview.getOverviewLastDay().isBefore(date);
                }else{
                    return overview.getOverviewLastDay().isAfter(LocalDate.now());
                }
            }).collect(Collectors.toList()));
        }
        return overviews;
    }

    public List<Visit> getVisits(Client client, LocalDate date) {
        Object[] currentClientCars = client.getCars().stream().filter(carsHasOwners -> {
            OwnershipStatus currentStatus = carsHasOwners.getStatus();
            return (currentStatus.equals(OwnershipStatus.CURRENT_OWNER) || currentStatus.equals(OwnershipStatus.COOWNER));
        }).toArray();
        List<Visit> overviews = new ArrayList<>();
        for (Object currentOwnership : currentClientCars) {
            Car car = ((CarsHasOwners) currentOwnership).getCar();
            overviews.addAll(car.getVisits().stream().filter(overview -> {
                if(date != null){
                    return overview.getVisitDate().isBefore(date) && overview.getVisitDate().isAfter(LocalDate.now());
                }else{
                    return overview.getVisitDate().isAfter(LocalDate.now());
                }
            }).collect(Collectors.toList()));
        }
        return overviews;
    }

    @POST
    @Path("/registerEmployee")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerEmployee(EmployeeRegisterForm newEmployeeData) {

        if (newEmployeeData.getPassword().equals(newEmployeeData.getConfirmPassword())) {
            String firstName = newEmployeeData.getFirstName();
            String lastName = newEmployeeData.getLastName();
            String email = newEmployeeData.getEmail();
            String password = newEmployeeData.getPassword();
            LocalDate hireDate = newEmployeeData.getHireDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Employee employee = new Employee(firstName, lastName, hireDate, null, password, email, EmployeeStatus.employed);
            employeesRepository.insert(employee);
            String token = employeesRepository.generateToken(employee);
            return Response.status(200).entity(new AccessTokenForm(token)).build();
        }
        return Response.status(400).entity(new ErrorResponse("Brak kompletnych danych rejestracji", null)).build();
    }

    @POST
    @Path("/signInEmployee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response signInEmployee(EmployeeSignInForm form) {
        if (form.getPassword() == null || form.getUsername() == null) {
            return Response.status(400).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Employee employee = employeesRepository.signIn(form.getPassword(), form.getUsername());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        employee.setLastLoggedIn(LocalDateTime.now());
        String accessToken = employeesRepository.generateToken(employee);
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
    }

    @POST
    @Path("/signOut")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response signOut(AccessTokenForm form) {
        repository.signOut(form.getAccessToken());
        return Response.status(200).build();
    }

    @POST
    @Path("/signOutEmployee")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response signOutEmployee(AccessTokenForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        employee.setAccessToken(null);
        repository.update(employee);
        return Response.status(200).build();
    }

    @POST
    @Path("/banUser")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response banUser(BanUserForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        Client client = repository.findClientByUsername(form.getUsername());
        if (client == null) {
            return Response.status(400).entity(new ErrorResponse("Klient o podanym mailu nie istnieje", accessToken)).build();
        }
        client.setAccessToken(null);
        client.setStatus(ClientStatus.BANNED);
        repository.update(client);
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
    }

    @POST
    @Path("/deleteAccount")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(AccessTokenForm form) {
        Client client = (Client) repository.findByToken(form.getAccessToken());
        if (client == null || client.getStatus().equals(ClientStatus.ACTIVE)) {
            return Response.status(401).entity(new ErrorResponse("Nie udało się autoryzować", null)).build();
        }
        client.setAccessToken(null);
        client.setStatus(ClientStatus.REMOVED);
        repository.update(client);
        return Response.status(200).build();
    }

    @POST
    @Path("/removeEmployee")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeEmployee(RemoveEmployeeForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Nie udało się autoryzować", null)).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        Employee employeeToRemove = employeesRepository.findByUsername(form.getEmployeeMail());
        if (employeeToRemove == null) {
            return Response.status(400).entity(new ErrorResponse("Nie istnieje konto o podanej nazwie", accessToken)).build();
        }
        employee.setStatus(EmployeeStatus.quit);
        LocalDate date = form.getQuitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        employee.setQuitDate(date);
        employeesRepository.update(employee);
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
    }

    @POST
    @Path("/checkEmail")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkIfEmailExists(CheckEmail checkEmail){
        String username = checkEmail.getEmail();
        Client client =repository.findClientByUsername(username);
        if(Objects.isNull(client)){
            return Response.status(400).entity(new ErrorResponse("Nie istnieje konto o podanej nazwie", null)).build();
        }else{
            return  Response.status(200).build();
        }
    }

}
