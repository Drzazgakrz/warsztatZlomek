package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.CarsRepository;
import pl.zlomek.warsztat.data.ClientsRepository;
import pl.zlomek.warsztat.data.CompaniesRepository;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.HashSet;
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

    private Logger log = LoggerFactory.getLogger(UpdateClient.class);

    @POST
    @Transactional
    @Path("/addCar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCar(CarDataForm carData){
        Client client = clientsRepository.findByToken(carData.getAccessToken());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)|| LocalDateTime.now().compareTo(client.getTokenExpiration())==1)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        Car car = carRepository.getCarByVin(carData.getVin());

        if(car!=null){
            List<CarsHasOwners> currentOwners = car.getOwners().stream().
                    filter((carsHasOwners -> carsHasOwners.getStatus().equals(OwnershipStatus.CURRENT_OWNER))).
                    collect(Collectors.toList());
            log.info("Wszyscy = "+Integer.toString(currentOwners.size()));
            OwnershipStatus status = (currentOwners.size()!= 0)? OwnershipStatus.NOT_VERIFIED_OWNER:OwnershipStatus.CURRENT_OWNER;
            CarsHasOwners cho = car.addCarOwner(client, status, carData.getRegistrationNumber());
            carRepository.insertOwnership(cho);
            carRepository.updateCar(car);
            String accessToken = clientsRepository.generateToken(client);
            return Response.status(200).entity(new PositiveResponse(accessToken)).build();
        }
        CarBrand carBrand = carRepository.getCarBrandByName(carData.getBrandName());
        car = new Car(carData.getRegistrationNumber(), carData.getVin(), carData.getModel(), carData.getProductionYear(), carBrand);
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
    public Response addClientToCompany(AddClientForm form){
        Client client = clientsRepository.findClientByUsername(form.getUsername());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE) || LocalDateTime.now().compareTo(client.getTokenExpiration())==1){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Company clientsCompany = companiesRepository.getCompanyByName(form.getCompanyName());

        if(clientsCompany == null){
            String accessToken = clientsRepository.generateToken(client);
            return Response.status(400).entity(new ErrorResponse("Firma o podanej nazwie nie istnieje", accessToken)).build();
        }
        if(client.getCompanies() == null){
            client.setCompanies(new HashSet<Company>());
        }
        client.getCompanies().add(clientsCompany);
        if(clientsCompany.getEmployees() == null){
            clientsCompany.setEmployees(new HashSet<Client>());
        }
        clientsCompany.getEmployees().add(client);
        String accessToken = clientsRepository.generateToken(client);
        companiesRepository.addClient(clientsCompany);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
    }
}
