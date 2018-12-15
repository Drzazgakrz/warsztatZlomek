package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.CarsRepository;
import pl.zlomek.warsztat.data.ClientsRepository;
import pl.zlomek.warsztat.data.CompaniesRepository;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/updateClient")
public class UpdateClient {

    @Inject
    private CarsRepository carRepository;

    @Inject
    private ClientsRepository clientsRepository;

    @Inject
    private CompaniesRepository companiesRepository;

    @Inject
    private EmployeesRepository employeesRepository;

    private Logger log = LoggerFactory.getLogger(UpdateClient.class);

    @POST
    @Transactional
    @Path("/addCar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCar(CarDataForm carData){
        Client client = (Client)clientsRepository.findByToken(carData.getAccessToken());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE))
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        if(!carData.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", carData.getAccessToken())).build();
        }
        Car car = carRepository.getCarByVin(carData.getVin());

        if(car!=null){
            List<CarsHasOwners> currentOwners = car.getOwners().stream().
                    filter((carsHasOwners -> carsHasOwners.getStatus().equals(OwnershipStatus.CURRENT_OWNER))).
                    collect(Collectors.toList());
            OwnershipStatus status = (currentOwners.size()!= 0)? OwnershipStatus.NOT_VERIFIED_OWNER:OwnershipStatus.CURRENT_OWNER;
            CarsHasOwners cho = car.addCarOwner(client, status, carData.getRegistrationNumber());
            carRepository.insertOwnership(cho);
            carRepository.updateCar(car);
            String accessToken = clientsRepository.generateToken(client);
            return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
        }
        CarBrand carBrand = carRepository.getCarBrandByName(carData.getBrandName());
        car = new Car(carData.getVin(), carData.getModel(), carData.getProductionYear(), carBrand);
        carRepository.insertCar(car);
        CarsHasOwners cho = car.addCarOwner(client, OwnershipStatus.CURRENT_OWNER, carData.getRegistrationNumber());
        carRepository.insertOwnership(cho);
        carRepository.updateCar(car);
        return Response.status(200).entity(new AccessTokenForm(carData.getAccessToken())).build();
    }

    @POST
    @Path("/addClientToCompany")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addClientToCompany(ClientCompanyForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Client client = clientsRepository.findClientByUsername(form.getUsername());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)){
            return Response.status(400).entity(new ErrorResponse("Klient o podanej nazwie użytkownika nie istnieje", null)).build();
        }
        Company clientsCompany = companiesRepository.getCompanyByName(form.getCompanyName());
        if(clientsCompany == null){
            String accessToken = clientsRepository.generateToken(client);
            return Response.status(400).entity(new ErrorResponse("Firma o podanej nazwie nie istnieje", accessToken)).build();
        }
        Object[] checkIfNotExists = client.getCompanies().stream().filter((currentCompany)->currentCompany.getClient().equals(client)).toArray();
        if(checkIfNotExists.length!=0 && ((CompaniesHasEmployees)checkIfNotExists[0]).getStatus().equals(EmploymentStatus.CURRENT_EMPLOYER)){
            return Response.status(400).entity(new ErrorResponse("Klient jest już zapisany do tej firmy", form.getAccessToken())).build();
        }else if(checkIfNotExists.length!=0 && ((CompaniesHasEmployees)checkIfNotExists[0]).getStatus().equals(EmploymentStatus.FORMER_EMPLOYER)){
            CompaniesHasEmployees che = (CompaniesHasEmployees)checkIfNotExists[0];
            che.setStatus(EmploymentStatus.CURRENT_EMPLOYER);
            companiesRepository.updateCompaniesEmployees(che);
        }else {
            CompaniesHasEmployees che = clientsCompany.addClientToCompany(client);
            companiesRepository.insertCompanyEmployee(che);
            companiesRepository.updateCompany(clientsCompany);
        }
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Path("/removeCar")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response removeCarFromClientsProfile(RemoveCarForm form){
        try{
            Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
            if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)){
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            }
            if(!form.validate()){
                return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
            }
            Car car = carRepository.getCarById(form.getCarId());
            if(car == null){
                return Response.status(400).entity(new ErrorResponse("Brak podanego samochodu", form.getAccessToken())).build();
            }
            Object[] cho;
            if( (cho = client.checkCar(car,client)).length<1){
                return Response.status(403).entity(new ErrorResponse("Samochód nie należy do tego klienta", form.getAccessToken())).build();
            }
            CarsHasOwners ownership = ((CarsHasOwners) cho[0]);
            ownership.setStatus(OwnershipStatus.FORMER_OWNER);
            carRepository.updateOwnership(ownership);
            return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
        }catch (Exception e){
            return Response.status(500).entity(new ErrorResponse("Błąd serwera. Przepraszamy", null)).build();
        }
    }

    @POST
    @Path("/removeClientFromCompany")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response removeClientFromCompany(ClientCompanyForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Client client = clientsRepository.findClientByUsername(form.getUsername());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)){
            return Response.status(400).entity(new ErrorResponse("Klient o podanej nazwie użytkownika nie istnieje", form.getAccessToken())).build();
        }
        Company clientsCompany = companiesRepository.getCompanyByName(form.getCompanyName());
        if(clientsCompany == null){
            return Response.status(400).entity(new ErrorResponse("Firma o podanej nazwie nie istnieje", form.getAccessToken())).build();
        }
        Object[] che = client.getCompanies().stream().filter((currentCompany)->currentCompany.getClient().equals(client)&&
                currentCompany.getStatus().equals(EmploymentStatus.CURRENT_EMPLOYER)).toArray();
        if(che.length == 0){
            return Response.status(403).entity(new ErrorResponse("Klient nie jest pracownikiem tej firmy", form.getAccessToken())).build();
        }
        CompaniesHasEmployees current = (CompaniesHasEmployees)che[0];
        current.setStatus(EmploymentStatus.FORMER_EMPLOYER);
        companiesRepository.updateCompaniesEmployees(current);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Path("/editClientData")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response editClientData(EditClientDataForm form){
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if(client == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        if(!form.validate()) {
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }

        if(form.getAptNum() != null && !form.getAptNum().equals(client.getAptNum())){
            client.setAptNum(form.getAptNum());
        }
        if(form.getBuildNum() != null && !client.getBuildNum().equals(form.getBuildNum())){
            client.setBuildNum(form.getBuildNum());
        }
        if(form.getCityName() != null && !client.getCityName().equals(form.getCityName())){
            client.setCityName(form.getCityName());
        }
        if(form.getFirstName() != null && !client.getFirstName().equals(form.getFirstName())){
            client.setFirstName(form.getFirstName());
        }
        if(form.getLastName() != null && !client.getLastName().equals(form.getLastName())){
            client.setLastName(form.getLastName());
        }
        if(form.getPhoneNumber() != null && !client.getPhoneNumber().equals(form.getPhoneNumber())){
            client.setPhoneNumber(form.getPhoneNumber());
        }
        if(form.getStreetName() != null && !client.getStreetName().equals(form.getStreetName())){
            client.setStreetName(form.getStreetName());
        }
        if(form.getZipCode() != null && !client.getZipCode().equals(form.getZipCode())){
            client.setZipCode(form.getZipCode());
        }
        if(form.getPassword()!= null && form.getPassword().equals(form.getConfirmPassword()) && !form.getPassword().equals(client.getPassword())){
            client.setPassword(Account.hashPassword(form.getPassword()));
        }
        clientsRepository.update(client);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/verifyCarOwnership")
    public Response verifyCarOwnership(VerificationForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        List<ClientResponse> owners = Arrays.asList(form.getOwners());
        OwnershipStatus ownershipStatus = (owners.size()>1)?OwnershipStatus.COOWNER: OwnershipStatus.CURRENT_OWNER;
        Car car = carRepository.getCarById(form.getCar().getId());
        owners.forEach(owner ->{
            Client client = clientsRepository.findClientByUsername(owner.getEmail());
            Object[] carList = client.getCars().stream().filter(cho-> cho.getCar().equals(car)).toArray();
            CarsHasOwners cho = ((CarsHasOwners)carList[0]);
            cho.setStatus(ownershipStatus);
            carRepository.updateOwnership(cho);
        });
        List<ClientResponse> notOwners = Arrays.asList(form.getNotOwners());
        notOwners.forEach(owner ->{
            Client client = clientsRepository.findClientByUsername(owner.getEmail());
            Object[] carList = client.getCars().stream().filter(cho-> cho.getCar().equals(car)).toArray();
            CarsHasOwners cho = ((CarsHasOwners)carList[0]);
            if(cho.getStatus().equals(OwnershipStatus.NOT_VERIFIED_OWNER)){
                carRepository.deleteOwnership(cho);
            }
            else{
                cho.setEndOwnershipDate(LocalDate.now());
                cho.setStatus(OwnershipStatus.FORMER_OWNER);
                carRepository.updateOwnership(cho);
            }
        });
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }
}
