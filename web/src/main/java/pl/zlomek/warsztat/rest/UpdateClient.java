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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;

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
    public Response addCar(CarDataForm carData){
        Client client = clientsRepository.findByToken(carData.getAccessToken());
        if(client == null)
            return Response.status(403).build();
        Car car = carRepository.getCarByVin(carData.getVin());

        if(car!=null){
            String accessToken = clientsRepository.generateToken(client);
            return Response.status(400).entity(accessToken).build();
        }

        CarBrand carBrand = carRepository.getCarBrandByName(carData.getBrandName());
        car = new Car(carData.getRegistrationNumber(), carData.getVin(), carData.getModel(), carData.getProductionYear(), carBrand);

        CarsHasOwners cho = car.addCarOwner(client);
        carRepository.insertOwnership(cho);
        carRepository.insertCar(car);
        String token = clientsRepository.generateToken(client);
        return Response.ok(token).build();
    }
    @POST
    @Path("/addClientToCompany")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addClientToCompany(AddClientForm form){
        Client client = clientsRepository.findClientByUsername(form.getUsername());
        if(client == null){
            return Response.status(401).build();
        }
        Company clientsCompany = companiesRepository.getCompanyByName(form.getCompanyName());
        if(clientsCompany == null){
            return Response.status(400).build();
        }
        if(client.getCompanies() == null){
            client.setCompanies(new HashSet<Company>());
        }
        client.getCompanies().add(clientsCompany);
        if(clientsCompany.getEmployees() == null){
            clientsCompany.setEmployees(new HashSet<Client>());
        }
        clientsCompany.getEmployees().add(client);
        clientsRepository.update(client);
        companiesRepository.addClient(clientsCompany);
        return Response.ok().build();
    }
}
