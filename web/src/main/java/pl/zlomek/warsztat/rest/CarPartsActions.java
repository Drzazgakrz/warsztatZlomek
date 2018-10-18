package pl.zlomek.warsztat.rest;

import pl.zlomek.warsztat.data.CarPartsRepository;
import pl.zlomek.warsztat.model.*;
import pl.zlomek.warsztat.data.EmployeesRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;


@Path("/CarParts")
public class CarPartsActions {
    @Inject
    private CarPartsRepository carPartsRepository;

    @Inject
    private EmployeesRepository employeesRepository;

    //ścieżka localhost:8080/warsztatZlomek/rest/CarParts/addCarPart
    @POST
    @Path("/addCarPart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCarPart(AddCarPartsForm newPart){
        Employee employee = (Employee) employeesRepository.findByToken(newPart.getAccessToken());
        if(employee == null)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        String accessToken = employeesRepository.generateToken(employee);
        if(!newPart.getName().isEmpty())
        {
            String name = newPart.getName();
            CarPart part = new CarPart(name, newPart.getTax(), newPart.getProducer());

            if(carPartsRepository.getCarPartByName(name) == null)
            {
                carPartsRepository.saveCarPart(part);

                return Response.status(200).entity(new PositiveResponse(accessToken)).build();
            }
            else
                return Response.status(409).entity(new ErrorResponse("Część istnieje w bazie", accessToken)).build();
        }
        return Response.status(400).entity(new ErrorResponse("Nie zdefiniowano nazwy części", accessToken)).build();
    }

}
