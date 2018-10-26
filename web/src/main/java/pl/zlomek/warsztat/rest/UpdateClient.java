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
            return Response.status(200).entity(new PositiveResponse(accessToken)).build();
        }
        CarBrand carBrand = carRepository.getCarBrandByName(carData.getBrandName());
        car = new Car(carData.getVin(), carData.getModel(), carData.getProductionYear(), carBrand);
        carRepository.insertCar(car);
        CarsHasOwners cho = car.addCarOwner(client, OwnershipStatus.CURRENT_OWNER, carData.getRegistrationNumber());
        carRepository.insertOwnership(cho);
        carRepository.updateCar(car);
        String token = clientsRepository.generateToken(client);
        return Response.status(200).entity(new PositiveResponse(token)).build();
    }

    @POST
    @Path("/addClientToCompany")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addClientToCompany(ClientCompanyForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getEmployeeToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
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
        Object[] checkIfNotExists = client.getCompanies().stream().filter((currentCompany)->currentCompany.getClient().equals(client)&&
                currentCompany.getStatus().equals(EmploymentStatus.CURRENT_EMPLOYER)).toArray();
        if(checkIfNotExists.length!=0){
            return Response.status(400).entity(new ErrorResponse("Klient jest już zapisany do tej firmy", form.getEmployeeToken())).build();
        }
        CompaniesHasEmployees che = clientsCompany.addClientToCompany(client);
        companiesRepository.insertCompanyEmployee(che);
        String accessToken = clientsRepository.generateToken(client);
        companiesRepository.updateCompany(clientsCompany);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
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
            String accessToken = clientsRepository.generateToken(client);
            Car car = carRepository.getCarById(form.getCarId());
            if(car == null){
                return Response.status(400).entity(new ErrorResponse("Brak podanego samochodu", accessToken)).build();
            }
            Object[] cho;
            if( (cho = client.checkCar(car)).length<1){
                return Response.status(403).entity(new ErrorResponse("Samochód nie należy do tego klienta", accessToken)).build();
            }
            CarsHasOwners ownership = ((CarsHasOwners) cho[0]);
            ownership.setStatus(OwnershipStatus.FORMER_OWNER);
            carRepository.updateOwnership(ownership);
            return Response.status(200).entity(new PositiveResponse(accessToken)).build();
        }catch (Exception e){
            return Response.status(500).entity(new ErrorResponse("Błąd serwera. Przepraszamy", null)).build();
        }
    }

    @POST
    @Path("/removeClientFromCompany")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response removeClientFromCompany(ClientCompanyForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getEmployeeToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Client client = clientsRepository.findClientByUsername(form.getUsername());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)){
            return Response.status(400).entity(new ErrorResponse("Klient o podanej nazwie użytkownika nie istnieje", form.getEmployeeToken())).build();
        }
        Company clientsCompany = companiesRepository.getCompanyByName(form.getCompanyName());
        if(clientsCompany == null){
            return Response.status(400).entity(new ErrorResponse("Firma o podanej nazwie nie istnieje", form.getEmployeeToken())).build();
        }
        Object[] che = client.getCompanies().stream().filter((currentCompany)->currentCompany.getClient().equals(client)&&
                currentCompany.getStatus().equals(EmploymentStatus.CURRENT_EMPLOYER)).toArray();
        if(che.length == 0){
            return Response.status(403).entity(new ErrorResponse("Klient nie jest pracownikiem tej firmy", form.getEmployeeToken())).build();
        }
        CompaniesHasEmployees current = (CompaniesHasEmployees)che[0];
        current.setStatus(EmploymentStatus.FORMER_EMPLOYER);
        companiesRepository.updateCompaniesEmployees(current);
        return Response.status(200).entity(new PositiveResponse(form.getEmployeeToken())).build();
    }
}
