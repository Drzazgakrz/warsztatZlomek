package pl.zlomek.warsztat.rest;

import pl.zlomek.warsztat.data.CarPartsRepository;
import pl.zlomek.warsztat.model.CarPart;
import pl.zlomek.warsztat.model.AddCarPartsForm;
import pl.zlomek.warsztat.model.Employee;
import pl.zlomek.warsztat.data.EmployeesRepository;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


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
    public Response addCarPart(AddCarPartsForm newPart){
        Employee employee = employeesRepository.findByToken(newPart.getAccessToken());
        if(employee == null )
            return Response.status(403).build();

        if(!newPart.getName().isEmpty())
        {
            String name = newPart.getName();
            CarPart part = new CarPart(name);

            if(carPartsRepository.getCarPartByName(name) == null)
            {
                carPartsRepository.saveCarPart(part);
                String accessToken = employeesRepository.generateToken(employee);
                return Response.status(200).entity(accessToken).build();
            }
            else
                return Response.status(409).build();
        }
        return Response.status(400).build();
    }

}
