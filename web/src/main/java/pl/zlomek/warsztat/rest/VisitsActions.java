package pl.zlomek.warsztat.rest;


import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.VisitResponseModel;
import pl.zlomek.warsztat.data.*;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.slf4j.Logger;


@Path("/visits")
public class VisitsActions {

    @Inject
    private ClientsRepository clientsRepository;

    @Inject
    private VisitsRepository visitsRepository;

    @Inject
    private CarsRepository carsRepository;


    @Inject
    private EmployeesRepository employeesRepository;

    @Inject
    private CarPartsRepository carPartsRepository;

    private Logger log = LoggerFactory.getLogger(VisitsActions.class);

    @POST
    @Transactional
    @Path("/submit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitVisit(SubmitVisitForm form){
        try {
            Employee employee = employeesRepository.findByToken(form.getAccessToken());
            if (employee == null || LocalDateTime.now().compareTo(employee.getTokenExpiration())==1) {
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            }
            String accessToken = employeesRepository.generateToken(employee);
            Visit visit = visitsRepository.getVisitById(form.getVisitId());
            if (visit == null) {
                return Response.status(400).entity(new ErrorResponse("Brak wizyty o podanym ID", accessToken)).entity(accessToken).build();
            }
            Overview overview = visit.getOverview();
            if (overview != null && form.getCountYears() != null) {
                overview.addTerminateOverview(form.getCountYears());
            } else if (overview != null && form.getCountYears() == null) {
                return Response.status(400).entity(new ErrorResponse("Przegląd powinien mieć termin ważności", accessToken)).build();
            }
            if(visit.getStatus()!= VisitStatus.IN_PROGRESS){
                return Response.status(400).entity(new ErrorResponse("Wizyta nie została zaakceptowana lub została już zakończona", accessToken)).build();
            }
            visit.setStatus(VisitStatus.FINISHED);
            if(form.getCarParts() != null) {
                List<CarPartModel> carPartModelList = Arrays.asList(form.getCarParts());
                carPartModelList.forEach(carPartModel -> {
                    CarPart carPart = carPartsRepository.getCarPartByName(carPartModel.getName());
                    if (carPart == null) {
                        return;
                    }
                    visit.addPartToVisit(carPart, carPartModel.getCount(), carPartModel.getPrice());
                    carPartsRepository.updateCarPart(carPart);
                });
            }
            visitsRepository.updateVisit(visit);
            return Response.status(200).entity(new PositiveResponse(accessToken)).build();
        }catch (Exception e){
            return Response.status(500).entity("Wystąpił nieznany błąd. Przepraszamy.").build();
        }
    }

    @POST
    @Transactional
    @Path("/addEmployee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEmployee(AddEmployeeForm form){

        Employee employee = employeesRepository.findByToken(form.getAccessToken());
        if(employee == null || LocalDateTime.now().compareTo(employee.getTokenExpiration())==1){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if(visit == null || !visit.getStatus().equals(VisitStatus.ACCEPTED)){
            String accessToken = employeesRepository.generateToken(employee);
            return Response.status(400).entity(new ErrorResponse("Wizyta nie istnieje lub zostałą wybrana przez innego pracownika",accessToken)).build();
        }
        visit.setStatus(VisitStatus.IN_PROGRESS);
        visit.setEmployee(employee);
        visitsRepository.updateVisit(visit);
        employee.getVisits().add(visit);
        employeesRepository.update(employee);
        String accessToken = employeesRepository.generateToken(employee);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
    }

    @POST
    @Transactional
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVisit(CreateVisitForm form){
        Client client = clientsRepository.findByToken(form.getAccessToken());
        if (client == null || !client.getStatus().equals(ClientStatus.ACTIVE) || LocalDateTime.now().compareTo(client.getTokenExpiration())==1)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        Car car = carsRepository.getCarById(form.getCarId());
        String accessToken = clientsRepository.generateToken(client);
        if(car == null) {
            return Response.status(400).entity(new ErrorResponse("Podany samochód nie istnieje", accessToken)).build();
        }
        if(!client.checkCar(car)){
            return Response.status(403).entity(new ErrorResponse("Samochód nie należy do tego klienta", accessToken)).build();
        }
        Overview overview = null;
        LocalDate visitDate = form.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if(form.isOverview()){
            overview = new Overview(visitDate, car);
            visitsRepository.createOverview(overview);
        }
        Visit visit = new Visit(visitDate, car, overview);
        car.getVisits().add(visit);
        carsRepository.updateCar(car);
        visitsRepository.createVisit(visit);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
    }

    @POST
    @Path("/removeVisit")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeVisit(ReomoveVisitForm form){
        Client client = clientsRepository.findByToken(form.getAccessToken());
        if(client == null || LocalDateTime.now().compareTo(client.getTokenExpiration())==1){
            return Response.status(401).build();
        }
        String accessToken = clientsRepository.generateToken(client);
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if(visit == null){
            return Response.status(400).entity(new ErrorResponse("Wizyta o podanym id nie istnieje", accessToken)).build();
        }else if(!visit.getStatus().equals(VisitStatus.ACCEPTED)){
            return Response.status(400).entity(new ErrorResponse("Wizyta została zaakceptowana. Nie można jej usunąć", accessToken)).build();
        }
        visitsRepository.removeVisit(visit);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
    }

    @POST
    @Path("/getAllClientsVisits")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClientsVisits(GetAllClientsVisitsForm form){
        try {
            Client client = clientsRepository.findByToken(form.getAccessToken());
            if (client == null || LocalDateTime.now().compareTo(client.getTokenExpiration())==1) {
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
            }
            String accessToken = clientsRepository.generateToken(client);
            List<Visit> visits = visitsRepository.getClientVisits(client);
            /*List<Visit> visits = new ArrayList<>();
            List<Visit> allVisits = visitsRepository.getAllVisits();
            System.out.println(allVisits.size());
            client.getCars().forEach(carsHasOwners -> {
                Car car = carsHasOwners.getCar();
                visits.addAll(car.getVisits());
            });*/
            log.info("Wszystkie" + Integer.toString(client.getCars().size()));
            log.info("Lista" + Integer.toString(visits.size()));
            VisitResponseModel[] visitsArray = visitsListToArray(visits);
            return Response.status(200).entity(new GetAllVisitsResponse(accessToken, visitsArray)).build();
        }catch (Exception e){
            return Response.status(500).entity(new ErrorResponse("Wystąpił nieoczekiwany błąd przepraszamy", null)).build();
        }
    }

    public VisitResponseModel[] visitsListToArray(Collection<Visit> visitsList){
        VisitResponseModel[] visits = new VisitResponseModel[visitsList.size()];
        int i = 0;
        for(Visit visit: visitsList){
            visits[i] = new VisitResponseModel(visit);
            i++;
        }
        return visits;
    }

    @POST
    @Path("/getAllCarVisits")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCarVisits(GetAllCarVisitsForm form){
        Car car = carsRepository.getCarByVin(form.getVin());
        if (car == null){
            return Response.status(400).entity(new ErrorResponse("Brak samochodów o podanym numerze VIN", null)).build();
        }
        log.info(Integer.toString(car.getVisits().size()));
        VisitResponseModel[] visits = visitsListToArray(car.getVisits());

        return Response.status(200).entity(new GetAllVisitsResponse(null, visits)).build();
    }
}
