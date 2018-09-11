package pl.zlomek.warsztat.rest;


import pl.zlomek.warsztat.data.CarsRepository;
import pl.zlomek.warsztat.data.ClientsRepository;
import pl.zlomek.warsztat.data.VisitsRepository;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/visits")
public class VisitsActions {

    @Inject
    private ClientsRepository clientsRepository;

    @Inject
    private VisitsRepository visitsRepository;

    @Inject
    private CarsRepository carsRepository;

    /*@POST
    @Path("/submit")
    public Response submitVisit(SubmitVisitForm form){

    }*/

    @POST
    @Transactional
    @Path("/add")
    public Response addVisit(CreateVisitForm form){
        Client client = clientsRepository.findByToken(form.getAccessToken());
        if (client == null)
            return Response.status(403).build();
        Car car = carsRepository.getCarById(form.getCarId());
        String accessToken = clientsRepository.generateToken(client);
        if(car == null || !client.getCars().contains(car))
            return Response.status(400).entity(accessToken).build();

        Visit visit = new Visit(form.getVisitDate(), car);
        visitsRepository.createVisit(visit);
        return Response.status(200).entity(accessToken).build();
    }
}
