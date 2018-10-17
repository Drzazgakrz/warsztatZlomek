package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.*;
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


@Path("/car")
public class CarActions {
    @Inject
    private CarPartsRepository carPartsRepository;

    @Inject
    private EmployeesRepository employeesRepository;

    @Inject
    private CarBrandsRespository carBrandsRespository;

    @Inject
    private ClientsRepository clientsRepository;

    @Inject
    private CarsRepository carsRepository;

    @Inject
    private CompaniesRepository companiesRepository;

    Logger log = LoggerFactory.getLogger(CarActions.class);

    //ścieżka localhost:8080/warsztatZlomek/rest/CarParts/addCarPart
    @POST
    @Path("/addCarPart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCarPart(AddCarPartsForm newPart){
        Employee employee = (Employee) employeesRepository.findByToken(newPart.getAccessToken());
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
        Employee employee = (Employee) employeesRepository.findByToken(newBrand.getAccessToken());
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

    @POST
    @Path("/addCarToCompany")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCarToCompany(AddCarToCompanyForm form){
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if(client == null )
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        String accessToken = employeesRepository.generateToken(client);
        Car car = carsRepository.getCarById(form.getCarId());
        if(car==null){
            return Response.status(400).entity(new ErrorResponse("Brak podanego samochodu w bazie", accessToken)).build();
        }
        if(client.checkCar(car).length<1){
            return Response.status(403).entity(new ErrorResponse("Samochód nie należy do tego klienta", accessToken)).build();
        }
        Company company = companiesRepository.getCompanyById(form.getCompanyId());
        if(!client.getCompanies().contains(company)){
            return Response.status(403).entity(new ErrorResponse("Firma nie jest dodana do tego klienta", accessToken)).build();
        }
        CompaniesHasCars companiesHasCars = company.addCar(car);
        companiesRepository.insertCarInJoinTable(companiesHasCars);
        companiesRepository.updateCompany(company);
        carsRepository.updateCar(car);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
    }
    @POST
    @Path("/removeCarFromCompany")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response removeCarFromCompany(RemoveCarFromCompanyForm form){
        try{
            Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
            if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)){
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            }
            String accessToken = clientsRepository.generateToken(client);
            Car car = carsRepository.getCarById(form.getCarId());
            if(car == null){
                return Response.status(400).entity(new ErrorResponse("Brak podanego samochodu", accessToken)).build();
            }
            Company company = companiesRepository.getCompanyById(form.getCompanyId());
            if(!client.getCompanies().contains(company)){
                return Response.status(403).entity(new ErrorResponse("Firma nie jest dodana do tego klienta", accessToken)).build();
            }
            log.info("Samochody"+Integer.toString(company.getCars().size()));
            log.info("Vin:" + car.getVin());
            Object[] companiesArray = company.getCars().stream().filter((chc)-> {
                log.info("Vin1"+chc.getCar().getVin());
                return chc.getCar().equals(car);}).toArray();
            if(companiesArray.length<1){
                return Response.status(400).entity(new ErrorResponse("Firma nie posiada tego samochodu", accessToken)).build();
            }
            CompaniesHasCars chc = (CompaniesHasCars)companiesArray[0];
            chc.setStatus(CompanyOwnershipStatus.FORMER_OWNER_COMPANY);
            companiesRepository.updateJoinTable(chc);
            return Response.status(200).entity(new PositiveResponse(accessToken)).build();
        }catch (Exception e){
            return Response.status(500).entity(new ErrorResponse("Błąd serwera. Przepraszamy", null)).build();
        }
    }
}