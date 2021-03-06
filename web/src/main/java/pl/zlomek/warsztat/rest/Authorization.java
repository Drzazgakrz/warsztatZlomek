package pl.zlomek.warsztat.rest;

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
import java.util.Collection;
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


    @Inject
    private VisitsActions visitsActions;
    //ścieżka localhost:8080/warsztatZlomek/rest/authorization/register
    @POST
    @Transactional
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(ClientForm newUserData) {

        Client client = repository.findClientByUsername(newUserData.getEmail());
        if(client != null){
            return Response.status(400).entity(new ErrorResponse("Klient o podanym adresie email już istnieje", null)).build();
        }

        if(!newUserData.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", null)).build();
        }
        if (newUserData.getPassword().equals(newUserData.getConfirmPassword())) {
            client = new Client(newUserData.getFirstName(), newUserData.getLastName(), newUserData.getEmail(),
                    newUserData.getPhoneNumber(), newUserData.getCityName(), newUserData.getStreetName(),
                    newUserData.getBuildNum(), newUserData.getAptNum(), newUserData.getZipCode(), newUserData.getPassword(),
                    null);
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
        if (signInForm.getPassword() != null || signInForm.getEmail() != null) {
            if(!signInForm.validate()){
                return Response.status(400).entity(new ErrorResponse("Błędne dane", null)).build();
            }
            Client client = repository.signIn(signInForm.getEmail(), signInForm.getPassword());
            if (client == null || !client.getStatus().equals(ClientStatus.ACTIVE)) {
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            }
            OverviewResponse[] overviews = overviewsListToArray(getOverviews(client, LocalDate.now().plusMonths(1)), client);

            VisitDetailsResponse[] visitsArray = visitsActions.visitsListToArray(getVisits(client, LocalDate.now().plusDays(7)));
            client.setLastLoggedIn(LocalDateTime.now());
            String token = repository.generateToken(client);
            return Response.status(200).entity(new SignInResponse(token, overviews,visitsArray)).build();
        }
        return Response.status(400).entity(new ErrorResponse("Brak kompletnych danych logowania", null)).build();
    }

    public OverviewResponse[] overviewsListToArray(Collection<Overview> overviews, Client client){
        OverviewResponse[] overviewsArray = new OverviewResponse[overviews.size()];
        int i = 0;
        for(Overview overview : overviews){
            Object[] car = client.getCars().stream().filter((carsHasOwners -> carsHasOwners.getCar().equals(overview.getCar()))).toArray();
            overviewsArray[i] = new OverviewResponse(overview, overview.getCar(), ((CarsHasOwners)car[0]).getRegistrationNumber());
            i++;
        }
        return overviewsArray;
    }

    public List<CarsHasOwners> getCarsList(Client client){
        return client.getCars().stream().filter(carsHasOwners -> {
            OwnershipStatus currentStatus = carsHasOwners.getStatus();
            return (currentStatus.equals(OwnershipStatus.CURRENT_OWNER) || currentStatus.equals(OwnershipStatus.COOWNER));
        }).collect(Collectors.toList());
    }

    public List<Overview> getOverviews(Client client, LocalDate date) {
        List<Overview> overviews = new ArrayList<>();
        for (CarsHasOwners currentOwnership : getCarsList(client)) {
            Car car = currentOwnership.getCar();
            overviews.addAll(car.getOverviews().stream().filter(overview -> {
                if(date != null && overview.getOverviewLastDay()!=null){
                    return overview.getOverviewLastDay().isBefore(date);
                }else if(overview.getOverviewLastDay()!=null){
                    return overview.getOverviewLastDay().isAfter(LocalDate.now());
                }
                return false;
            }).collect(Collectors.toList()));
        }
        return overviews;
    }

    public List<Visit> getVisits(Client client, LocalDate date) {
        List<Visit> overviews = new ArrayList<>();
        for (CarsHasOwners currentOwnership : getCarsList(client)) {
            Car car =  currentOwnership.getCar();
            overviews.addAll(car.getVisits().stream().filter(overview -> {
                if(date != null){
                    return overview.getVisitDate().toLocalDate().isBefore(date) && overview.getVisitDate().isAfter(LocalDateTime.now());
                }else{
                    return overview.getVisitDate().isAfter(LocalDateTime.now());
                }
            }).collect(Collectors.toList()));
        }
        return overviews;
    }

    @POST
    @Path("/getFutureVisits")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getFutureVisits(AccessTokenForm form){
        Client client = (Client) repository.findByToken(form.getAccessToken());
        if(client == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        VisitDetailsResponse[] visits = visitsActions.visitsListToArray(getVisits(client, null));
        return Response.status(200).entity(new GetVisitsResponse(form.getAccessToken(), visits)).build();
    }

    @POST
    @Path("/registerEmployee")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerEmployee(EmployeeRegisterForm newEmployeeData) {

        Employee employee = (Employee) employeesRepository.findByToken(newEmployeeData.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Employee checkEmployee = employeesRepository.findByUsername(newEmployeeData.getEmail());
        if (checkEmployee != null) {
            return Response.status(400).entity(new ErrorResponse("pracownik o podanym mailu istnieje", null)).build();
        }
        if (newEmployeeData.getPassword().equals(newEmployeeData.getConfirmPassword())) {
            if(!newEmployeeData.validate()){
                return Response.status(400).entity(new ErrorResponse("Błędne dane", newEmployeeData.getAccessToken())).build();
            }
            String firstName = newEmployeeData.getFirstName();
            String lastName = newEmployeeData.getLastName();
            String email = newEmployeeData.getEmail();
            String password = newEmployeeData.getPassword();
            LocalDate hireDate = newEmployeeData.getHireDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Employee newEmployee = new Employee(firstName, lastName, hireDate, null, password, email, EmployeeStatus.employed);
            employeesRepository.insert(newEmployee);
            return Response.status(200).entity(new AccessTokenForm(newEmployeeData.getAccessToken())).build();
        }
        return Response.status(400).entity(new ErrorResponse("Brak kompletnych danych rejestracji", newEmployeeData.getAccessToken())).build();
    }

    @POST
    @Path("/signInEmployee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response signInEmployee(EmployeeSignInForm form) {
        if (form.getPassword() == null || form.getEmail() == null || !form.validate()) {
            return Response.status(400).entity(new ErrorResponse("Błędne dane", null)).build();
        }
        Employee employee = employeesRepository.signIn(form.getPassword(), form.getEmail());
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
        employeesRepository.signOut(form.getAccessToken());
        return Response.status(200).build();
    }

    @POST
    @Path("/banUser")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response banUser(GetClientForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Client client = repository.findClientByUsername(form.getUsername());
        if (client == null) {
            return Response.status(400).entity(new ErrorResponse("Klient o podanym mailu nie istnieje", form.getAccessToken())).build();
        }
        client.setAccessToken(null);
        client.setStatus(ClientStatus.BANNED);
        repository.update(client);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Path("/deleteAccount")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(AccessTokenForm form) {
        Client client = (Client) repository.findByToken(form.getAccessToken());
        if (client == null) {
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
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Employee employeeToRemove = employeesRepository.findByUsername(form.getEmployeeMail());
        if (employeeToRemove == null) {
            return Response.status(400).entity(new ErrorResponse("Nie istnieje konto o podanej nazwie", form.getAccessToken())).build();
        }
        employeeToRemove.setStatus(EmployeeStatus.quit);
        LocalDate date = form.getQuitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        employeeToRemove.setQuitDate(date);
        employeesRepository.update(employee);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Path("/checkEmail")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkIfEmailExists(CheckEmail checkEmail){
        String username = checkEmail.getEmail();
        if(!checkEmail.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", null)).build();
        }
        if(!checkEmail.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", null)).build();
        }
        Client client =repository.findClientByUsername(username);
        if(Objects.isNull(client)){
            return Response.status(400).entity(new ErrorResponse("Nie istnieje konto o podanej nazwie", null)).build();
        }else{
            return  Response.status(200).build();
        }
    }


    @POST
    @Path("/getFullClientData")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getFullClientData(AccessTokenForm form){
        Client client = (Client) repository.findByToken(form.getAccessToken());
        if(client == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
        }
        return Response.status(200).entity(new ClientResponse(client, form.getAccessToken())).build();
    }

    @POST
    @Path("/getClientData")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getClientData(GetClientForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się",
                    null)).build();
        }
        Client client = repository.findClientByUsername(form.getUsername());
        if(client == null){
            return Response.status(401).entity(new ErrorResponse("Klient o podanym mailu nie istnieje",
                    form.getAccessToken())).build();
        }
        return Response.status(200).entity(new ClientResponse(client, form.getAccessToken())).build();
    }
}
