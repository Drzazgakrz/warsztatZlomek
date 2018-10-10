package pl.zlomek.warsztat.rest;

import pl.zlomek.warsztat.data.CarBrandsRespository;
import pl.zlomek.warsztat.data.CarPartsRepository;
import pl.zlomek.warsztat.model.*;
import pl.zlomek.warsztat.data.EmployeesRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/car")
public class CarActions {
    @Inject
    private CarPartsRepository carPartsRepository;

    @Inject
    private EmployeesRepository employeesRepository;

    @Inject
    private CarBrandsRespository carBrandsRespository;

    //ścieżka localhost:8080/warsztatZlomek/rest/CarParts/addCarPart
    @POST
    @Path("/addCarPart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCarPart(AddCarPartsForm newPart){
        Employee employee = employeesRepository.findByToken(newPart.getAccessToken());
        if(employee == null )
            return Response.status(403).build();
        String accessToken = employeesRepository.generateToken(employee);

        if(!newPart.getName().isEmpty())
        {
            String name = newPart.getName();
            CarPart part = new CarPart(name);

            if(carPartsRepository.getCarPartByName(name) == null)
            {
                carPartsRepository.saveCarPart(part);
                return Response.status(200).entity(accessToken).build();
            }
            else
                return Response.status(409).entity(accessToken).build();
        }
        return Response.status(400).entity(accessToken).build();
    }

    @POST
    @Path("/addCarBrand")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCarBrand(AddCarBrandForm newBrand){
        Employee employee = employeesRepository.findByToken(newBrand.getAccessToken());
        if(employee == null )
            return Response.status(403).build();
        String accessToken = employeesRepository.generateToken(employee);

        if(!newBrand.getBrandName().isEmpty()){
            String name = newBrand.getBrandName();
            if(carBrandsRespository.getCarBrandByName(name) == null){
                CarBrand carBrand = new CarBrand(name);
                carBrandsRespository.saveCarBrand(carBrand);
                return Response.status(200).entity(accessToken).build();
            }
            else
                return Response.status(409).entity(accessToken).build();
        }
        return Response.status(400).entity(accessToken).build();
    }



}