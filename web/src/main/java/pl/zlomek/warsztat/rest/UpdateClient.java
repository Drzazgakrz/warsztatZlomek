package pl.zlomek.warsztat.rest;

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

@Path("/updateClient")
public class UpdateClient {

    @Inject
    private CarsRepository carRepository;

    @Inject
    private ClientsRepository clientsRepository;

    @Inject
    private CompaniesRepository companiesRepository;

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
        Company clientsCmpany = companiesRepository.getCompanyByName(form.getCompanyName());
        if(clientsCmpany == null){
            return Response.status(400).build();
        }
        client.getCompanies().add(clientsCmpany);
        clientsCmpany.getEmployees().add(client);
        String token = clientsRepository.generateToken(client);
        return Response.ok(token).build();
    }
}
