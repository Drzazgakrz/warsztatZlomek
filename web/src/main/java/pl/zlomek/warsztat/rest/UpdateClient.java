package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.CarsRepository;
import pl.zlomek.warsztat.data.ClientsRepository;
import pl.zlomek.warsztat.data.CompaniesRepository;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

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
    @Path("/addCar")
    public Response addCar(CarDataForm carData, String accessToken){
        Car car = carRepository.getCar(carData.getVin());
        //if(car==null){
          //  car = new Car(/*Uzupełnij konstruktor*/);
            //carRepository.insertCar(car);
        //}*/

        Client client = clientsRepository.findClientByToken(accessToken);
        String token = clientsRepository.generateToken(client);
        return Response.ok(token).build();
    }
    @POST
    @Path("/addClientToCompany")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addClientToCompany(AddClientForm form){
        Client client = clientsRepository.findClientByUsername(form.getUsername());
        if(client == null){
            return Response.status(403).build();
        }
        Company clientsCompany = companiesRepository.getCompanyByName(form.getCompanyName());
        if(clientsCompany == null){
            log.info("brak firmy");
            return Response.status(400).build();
        }
        if(client.getCompanies() == null){
            client.setCompanies(new ArrayList<Company>());
        }
        client.getCompanies().add(clientsCompany);
        log.info(client.toString()+" moje");
        if(clientsCompany.getEmployees() == null){
            clientsCompany.setEmployees(new ArrayList<Client>());
        }
        log.info(clientsCompany.toString()+" moje");
        clientsCompany.getEmployees().add(client);
        clientsRepository.addCompany(client);// coś tu się pierdoli
        companiesRepository.addClient(clientsCompany);
        return Response.ok().build();
    }
}
