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
        if(!newPart.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", newPart.getAccessToken())).build();
        }
        if(!newPart.getName().isEmpty())
        {
            String name = newPart.getName();
            CarPart part = new CarPart(name, newPart.getTax(), newPart.getProducer());

            if(carPartsRepository.getCarPartByName(name) == null)
            {
                carPartsRepository.saveCarPart(part);

                return Response.status(200).entity(new AccessTokenForm(newPart.getAccessToken())).build();
            }
            else
                return Response.status(409).entity(new ErrorResponse("Część istnieje w bazie", newPart.getAccessToken())).build();
        }
        return Response.status(400).entity(new ErrorResponse("Nie zdefiniowano nazwy części", newPart.getAccessToken())).build();
    }

    @POST
    @Path("/editCarPart")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response editCarPart(EditCarPartsForm form){
        try {
            Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
            if(employee == null)
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            if(!form.validate()){
                return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
            }
            CarPart part = carPartsRepository.getCarPartById(form.getCarPartId());
            if(part== null)
                return Response.status(404).entity(new ErrorResponse("Brak podanej części", form.getAccessToken())).build();
            if(form.getName()!= null && !part.getName().equals(form.getName())){
                part.setName(form.getName());
            }
            if(form.getProducer() != null &&!part.getProducer().equals( form.getProducer())){
                part.setProducer(form.getProducer());
            }
            if(form.getTax()!=0 && part.getTax()!= form.getTax()){
                part.setTax(form.getTax());
            }
            carPartsRepository.updateCarPart(part);
            return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
        }catch (Exception e){
            return Response.status(500).entity(new ErrorResponse("Wystąpił błąd", form.getAccessToken())).build();
        }

    }

}
