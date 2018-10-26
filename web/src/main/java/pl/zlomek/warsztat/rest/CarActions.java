package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.*;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


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
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
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
            return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
        }catch (Exception e){
            return Response.status(500).entity(new ErrorResponse("Błąd serwera. Przepraszamy", null)).build();
        }
    }

    @POST
    @Path("/addCoowner")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCoowner(CoownerForm form){
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        String accessToken = clientsRepository.generateToken(client);
        Client coowner = clientsRepository.findClientByUsername(form.getCoownerUsername());
        if(coowner == null){
            return Response.status(400).entity(new ErrorResponse("Brak klienta o podanym mailu", accessToken)).build();
        }
        Car car = carsRepository.getCarById(form.getCarId());
        if(car == null){
            return Response.status(400).entity(new ErrorResponse("brak podanego samochodu", accessToken)).build();
        }
        CarsHasOwners currentOwner = carsRepository.getOwnership(car.getId(), client.getClientId());
        if(currentOwner == null){
            return Response.status(403).entity(new ErrorResponse("Klient nie posiada tego samochodu", accessToken)).build();
        }
        currentOwner.setStatus(OwnershipStatus.COOWNER);
        CarsHasOwners cho = car.addCarOwner(coowner, OwnershipStatus.COOWNER, currentOwner.getRegistrationNumber());
        carsRepository.insertOwnership(cho);
        carsRepository.updateOwnership(currentOwner);
        carsRepository.updateCar(car);
        clientsRepository.update(coowner);
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
    }

    @POST
    @Path("/removeCoowner")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeCoowner(CoownerForm form){
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        String accessToken = clientsRepository.generateToken(client);
        Client coowner = clientsRepository.findClientByUsername(form.getCoownerUsername());
        if(coowner == null){
            return Response.status(400).entity(new ErrorResponse("Brak klienta o podanym mailu", accessToken)).build();
        }
        Car car = carsRepository.getCarById(form.getCarId());
        if(car == null){
            return Response.status(400).entity(new ErrorResponse("brak podanego samochodu", accessToken)).build();
        }
        List<CarsHasOwners> ownershipList = car.getOwners().stream().
                filter(cho-> cho.getOwner().equals(coowner)).collect(Collectors.toList());
        if(ownershipList.size()==0){
            return Response.status(404).
                    entity(new ErrorResponse("Ten klient nie jest współwłaścicielem tego samochodu", accessToken)).build();
        }
        ownershipList.forEach((cho)->{
            if(cho.getOwner().equals(coowner)) {
                cho.setEndOwnershipDate(LocalDate.now());
                cho.setStatus(OwnershipStatus.FORMER_OWNER);
                carsRepository.updateOwnership(cho);
            }
            else if(ownershipList.size() == 2 && cho.getOwner().equals(client)){
                cho.setStatus(OwnershipStatus.CURRENT_OWNER);
                carsRepository.updateOwnership(cho);
            }
        });
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Path("/getAllClientsCars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClientsCars(AccessToken form){
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if(client == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        CarResponseModel[] cars = new CarResponseModel[client.getCars().size()];
        int i = 0;
        for (CarsHasOwners cho: client.getCars()) {
            cars[i] =new CarResponseModel(cho.getCar(), cho.getRegistrationNumber());
            i++;
        }
        return Response.status(200).entity(new GetAllCarsResponse(form.getAccessToken(), cars)).build();
    }

    @POST
    @Path("/editCar")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response editCar(EditCarForm form){
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if(client == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Car car = carsRepository.getCarById(form.getCarId());
        if(car == null){
            return Response.status(400).entity(new ErrorResponse("Brak samochodu o podanym id", form.getAccessToken())).build();
        }

        List<CarsHasOwners> cars = client.getCars().stream().filter((cho)->cho.getOwner().equals(client)&&
                !cho.getStatus().equals(OwnershipStatus.FORMER_OWNER) && !cho.getStatus().equals(OwnershipStatus.NOT_VERIFIED_OWNER)
        ).collect(Collectors.toList());
        if(cars.size()==0){
            return Response.status(403).entity(new ErrorResponse("Podany samochód nie należy do klienta", form.getAccessToken())).build();
        }
        if(form.getModel()!= null && !form.getModel().equals(car.getModel())){
            car.setModel(form.getModel());
        }
        if(form.getProductionYear()!= 0 && form.getProductionYear()!= car.getProdYear()){
            car.setProdYear(form.getProductionYear());
        }
        for (CarsHasOwners cho : cars){
            if(form.getRegistrationNumber() != null && !form.getRegistrationNumber().equals(cho.getRegistrationNumber())){
                cho.setRegistrationNumber(form.getRegistrationNumber());
                carsRepository.updateOwnership(cho);
            }
        }
        carsRepository.updateCar(car);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getAllCarBrands")
    public Response getAllCarBrands(){
        List<CarBrand> brands =  carBrandsRespository.getAllCarBrands();
        String[] brandsArray = new String[brands.size()];
        int i = 0;
        for(CarBrand brand : brands){
            brandsArray[i] = brand.getBrandName();
            i++;
        }
        return Response.status(200).entity(new GetAllCarBrandsResponse(brandsArray)).build();
    }
}