package pl.zlomek.warsztat.rest;


import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.*;
import pl.zlomek.warsztat.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;


@Path("/visits")
@ApplicationScoped
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

    @Inject
    private ServicesRepository servicesRepository;

    private Logger log = LoggerFactory.getLogger(VisitsActions.class);

    private VisitStatus getVisitStatus(String newStatus) {
        switch (newStatus) {
            case ("new"):
                return VisitStatus.NEW;
            case ("accepted"):
                return VisitStatus.ACCEPTED;
            case ("in progress"):
                return VisitStatus.IN_PROGRESS;
            case ("for pickup"):
                return VisitStatus.FOR_PICKUP;
            case ("finished"):
                return VisitStatus.FINISHED;
            default:
                return null;
        }
    }

    @POST
    @Transactional
    @Path("/edit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editVisit(SubmitVisitForm form) {
        try {
            Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
            if (employee == null) {
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            }
            if(!form.validate()){
                return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
            }
            Visit visit = visitsRepository.getVisitById(form.getVisitId());
            if (visit == null) {
                return Response.status(400).entity(new ErrorResponse("Brak wizyty o podanym ID", form.getAccessToken())).build();
            }
            Overview overview = visit.getOverview();
            if (overview != null && form.getCountYears() != null) {
                Car car = visit.getCar();
                car.getOverviews().add(overview);
                carsRepository.updateCar(car);
                overview.addTerminateOverview(form.getCountYears());
            } else if (overview != null && form.getCountYears() == null &&
                    (form.getStatus().equals(VisitStatus.FOR_PICKUP.toString())||form.getStatus().equals(VisitStatus.FINISHED.toString()))) {
                return Response.status(400).entity(new ErrorResponse("Przegląd powinien mieć termin ważności", form.getAccessToken())).build();
            }
            VisitStatus status = getVisitStatus(form.getStatus());
            if(status!=null && !visit.getStatus().equals(status)){
                if(visit.getStatus().equals(VisitStatus.IN_PROGRESS) && status.equals(VisitStatus.FOR_PICKUP)){
                    visit.setVisitFinished(LocalDate.now());
                }
                visit.setStatus(status);
            }

            if (form.getCarParts() != null) {
                visit.getParts().clear();
                List<CarPartModel> carPartModelList = Arrays.asList(form.getCarParts());
                carPartModelList.forEach(carPartModel -> {
                    CarPart carPart = carPartsRepository.getCarPartById(carPartModel.getId());
                    if (carPart == null) {
                        return;
                    }
                    VisitsParts relation = visit.addPartToVisit(carPart, carPartModel.getCount(), carPartModel.getPrice());
                    visitsRepository.createVisitPart(relation);
                    carPartsRepository.updateCarPart(carPart);
                });
            }
            if (form.getServices() != null) {
                visit.getServices().clear();
                List<ServiceModel> carPartModelList = Arrays.asList(form.getServices());
                carPartModelList.forEach(serviceModel -> {
                    Service service = servicesRepository.getServiceByName(serviceModel.getName());
                    if (service == null) {
                        return;
                    }
                    VisitsHasServices relation = visit.addServiceToVisit(service, serviceModel.getCount(), new BigDecimal(serviceModel.getPrice()));
                    servicesRepository.insertVisitsServices(relation);
                    servicesRepository.updateService(service);
                });
            }
            visitsRepository.updateVisit(visit);
            return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
        } catch (Exception e) {
            return Response.status(500).entity("Wystąpił nieznany błąd. Przepraszamy.").build();
        }
    }

    @POST
    @Transactional
    @Path("/addEmployee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEmployee(AddEmployeeForm form) {

        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (visit == null || !visit.getStatus().equals(VisitStatus.NEW)) {
            return Response.status(400).entity(new ErrorResponse("Wizyta nie istnieje lub zostałą wybrana przez innego pracownika", form.getAccessToken())).build();
        }
        visit.setStatus(VisitStatus.ACCEPTED);
        visit.setEmployee(employee);
        visitsRepository.updateVisit(visit);
        employee.getVisits().add(visit);
        employeesRepository.update(employee);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Transactional
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVisit(CreateVisitForm form) {
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if (client == null || !client.getStatus().equals(ClientStatus.ACTIVE))
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        Car car = carsRepository.getCarById(form.getCarId());
        if (car == null) {
            return Response.status(400).entity(new ErrorResponse("Podany samochód nie istnieje", form.getAccessToken())).build();
        }
        if (client.checkCar(car).length < 1) {
            return Response.status(403).entity(new ErrorResponse("Samochód nie należy do tego klienta", form.getAccessToken())).build();
        }
        Overview overview = null;
        LocalDateTime visitDate = form.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(1);
        if (LocalDateTime.now().isAfter(visitDate))
            return Response.status(400).entity(new ErrorResponse("Data wizyty musi być późniejsza niż dzisiejsza data",
                    form.getAccessToken())).build();
        if (form.isOverview()) {
            overview = new Overview(visitDate, car);
            visitsRepository.createOverview(overview);
        }
        Visit visit = new Visit(visitDate, car, overview, client);
        car.getVisits().add(visit);
        carsRepository.updateCar(car);
        visitsRepository.createVisit(visit);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Path("/removeVisit")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeVisit(ReomoveVisitForm form) {
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if (client == null) {
            return Response.status(401).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (visit == null) {
            return Response.status(400).entity(new ErrorResponse("Wizyta o podanym id nie istnieje", form.getAccessToken())).build();
        } else if (visit.getStatus().equals(VisitStatus.NEW)) {
            return Response.status(400).entity(new ErrorResponse("Wizyta została zaakceptowana. Nie można jej usunąć", form.getAccessToken())).build();
        }
        visitsRepository.removeVisit(visit);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Path("/getAllClientsVisits")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getAllClientsVisits(AccessTokenForm form) {
        try {
            Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
            if (client == null) {
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
            }
            Set<Visit> visits = new HashSet<>();
            for (CarsHasOwners cho : client.getCars()) {
                visits.addAll(cho.getCar().getVisits().stream().filter(visit -> {
                    LocalDate end = (cho.getEndOwnershipDate() != null)?cho.getEndOwnershipDate() :LocalDate.now();
                    return visit.getVisitDate().toLocalDate().isAfter(cho.getBeginOwnershipDate()) && visit.getVisitDate().toLocalDate().isBefore(end);
                }).collect(Collectors.toList()));
            }
            VisitDetailsResponse[] visitsArray = visitsListToArray(visits);
            return Response.status(200).entity(new GetVisitsResponse(form.getAccessToken(), visitsArray)).build();
        } catch (Exception e) {
            return Response.status(500).entity(new ErrorResponse("Wystąpił nieoczekiwany błąd przepraszamy", null)).build();
        }
    }

    public VisitDetailsResponse[] visitsListToArray(Collection<Visit> visitsList) {
        VisitDetailsResponse[] visits = new VisitDetailsResponse[visitsList.size()];
        int i = 0;
        for (Visit visit : visitsList) {
            visits[i] = new VisitDetailsResponse(visit);
            i++;
        }
        return visits;
    }

    @POST
    @Path("/getAllCarVisits")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCarVisits(GetAllCarVisitsForm form) {
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane",null)).build();
        }
        Car car = carsRepository.getCarByVin(form.getVin());
        if (car == null) {
            return Response.status(400).entity(new ErrorResponse("Brak samochodów o podanym numerze VIN", null)).build();
        }
        VisitDetailsResponse[] visits = visitsListToArray(car.getVisits());
        return Response.status(200).entity(new GetVisitsResponse(null, visits)).build();
    }

    @POST
    @Path("/addService")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addService(AddServiceForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        visitsRepository.insertService(new Service(form.getName(), form.getTax()));
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }


    @POST
    @Path("/getAllEmployeeVisits")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getAllEmployeeVisits(AccessTokenForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
        }
        VisitDetailsResponse[] visits = visitsListToArray(employee.getVisits());
        return Response.status(200).entity(new GetVisitsResponse(form.getAccessToken(), visits)).build();
    }

    @POST
    @Path("/getSingleVisitDetails")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getSingleVisitDetails(GetSingleEmployeeVisitForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
        }
        Object[] visitsArray = employee.getVisits().stream().filter((visit -> visit.getId()==form.getVisitId())).toArray();
        if(visitsArray.length<1){
            return Response.status(403).entity(new ErrorResponse("Wizyta nie należy do tego pracownika", form.getAccessToken())).build();
        }
        VisitDetailsResponse visit = new VisitDetailsResponse((Visit) visitsArray[0]);
        return Response.status(200).entity(new GetSingleVisitDetails(form.getAccessToken(), visit)).build();
    }

    @GET
    @Path("/getDataForVisit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataForVisit(){
        List<CarPart> parts = carPartsRepository.getAllCarParts();
        CarPartModel[] carParts = new CarPartModel[parts.size()];
        int i = 0;
        for(CarPart currentPart : parts){
            carParts[i] = new CarPartModel(currentPart);
            i++;
        }
        List<Service> services = servicesRepository.getAllServices();
        ServiceModel[] servicesArray = new ServiceModel[services.size()];
        i = 0;
        for(Service currentService : services){
            servicesArray[i] = new ServiceModel(currentService);
            i++;
        }
        return Response.status(200).entity(new GetStuffForVisitsResponse(carParts, servicesArray)).build();
    }

    @POST
    @Path("/editService")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response editService(EditServiceForm form){
        try {
            Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
            if(employee == null)
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            if(!form.validate()){
                return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
            }
            Service service = servicesRepository.getServiceById(form.getId());
            if(service== null)
                return Response.status(404).entity(new ErrorResponse("Brak podanej części", form.getAccessToken())).build();
            if(form.getName()!= null && !service.getName().equals(form.getName())){
                service.setName(form.getName());
            }
            if(form.getTax()!=0 && service.getTax()!= form.getTax()){
                service.setTax(form.getTax());
            }
            servicesRepository.updateService(service);
            return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
        }catch (Exception e){
            return Response.status(500).entity(new ErrorResponse("Wystąpił błąd", form.getAccessToken())).build();
        }
    }

    @POST
    @Path("/getNewVisits")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getNewVisits(AccessTokenForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        VisitDetailsResponse[] visits = visitsListToArray(visitsRepository.getAllNewVisits());
        return Response.status(200).entity(new GetVisitsResponse(form.getAccessToken(),visits)).build();
    }

    @POST
    @Path("/getNotFinishedVisits")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotFinishedVisits(AccessTokenForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        List<Visit> notFinishedVisits = employee.getVisits().stream().filter(visit ->
                !visit.getStatus().equals(VisitStatus.NEW) && !visit.getStatus().equals(VisitStatus.FINISHED)).
                collect(Collectors.toList());
        VisitDetailsResponse[] visits = visitsListToArray(notFinishedVisits);
        return Response.status(200).entity(new GetVisitsResponse(form.getAccessToken(),visits)).build();
    }

    @POST
    @Path("/addEmptyVisit")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEmptyVisit(CreateVisitForm form){
        Employee employee = (Employee)employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Client client = clientsRepository.getClientById(0L);
        Car car = carsRepository.getCarById(0L);
        if(client == null || car == null){
            return Response.status(404).entity("Brak domyślnych danych").build();
        }
        LocalDateTime date = form.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Overview overview = null;
        if(form.isOverview()){
            overview = new Overview(date, car);
            visitsRepository.createOverview(overview);
        }
        Visit visit = new Visit(date, car, overview, client);
        visitsRepository.createVisit(visit);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }
}
