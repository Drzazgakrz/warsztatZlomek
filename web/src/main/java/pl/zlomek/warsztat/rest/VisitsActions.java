package pl.zlomek.warsztat.rest;


import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.model.VisitResponseModel;
import pl.zlomek.warsztat.data.*;
import pl.zlomek.warsztat.model.*;

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
            VisitStatus status = getVisitStatus(form.getStatus());
            if(status!=null){
                visit.setStatus(status);
            }

            if (form.getCarParts() != null) {
                visit.getParts().clear();
                List<CarPartModel> carPartModelList = Arrays.asList(form.getCarParts());
                carPartModelList.forEach(carPartModel -> {
                    CarPart carPart = carPartsRepository.getCarPartByName(carPartModel.getName());
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
                    Service service = servicesRepository.getServiceByName(serviceModel.getServiceName());
                    if (service == null) {
                        return;
                    }
                    VisitsHasServices relation = visit.addServiceToVisit(service, serviceModel.getCount(), new BigDecimal(serviceModel.getPrice()));
                    servicesRepository.insertVisitsServices(relation);
                    servicesRepository.updateService(service);
                });
            }
            visitsRepository.updateVisit(visit);
            return Response.status(200).entity(new PositiveResponse(accessToken)).build();
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
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (visit == null || !visit.getStatus().equals(VisitStatus.ACCEPTED)) {
            String accessToken = employeesRepository.generateToken(employee);
            return Response.status(400).entity(new ErrorResponse("Wizyta nie istnieje lub zostałą wybrana przez innego pracownika", accessToken)).build();
        }
        visit.setStatus(VisitStatus.ACCEPTED);
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
    public Response addVisit(CreateVisitForm form) {
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if (client == null || !client.getStatus().equals(ClientStatus.ACTIVE))
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        Car car = carsRepository.getCarById(form.getCarId());
        String accessToken = clientsRepository.generateToken(client);
        if (car == null) {
            return Response.status(400).entity(new ErrorResponse("Podany samochód nie istnieje", accessToken)).build();
        }
        if (client.checkCar(car).length < 1) {
            return Response.status(403).entity(new ErrorResponse("Samochód nie należy do tego klienta", accessToken)).build();
        }
        Overview overview = null;
        LocalDate visitDate = form.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (form.isOverview()) {
            overview = new Overview(visitDate, car);
            visitsRepository.createOverview(overview);
        }
        Visit visit = new Visit(visitDate, car, overview, client);
        car.getVisits().add(visit);
        carsRepository.updateCar(car);
        visitsRepository.createVisit(visit);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
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
        String accessToken = clientsRepository.generateToken(client);
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (visit == null) {
            return Response.status(400).entity(new ErrorResponse("Wizyta o podanym id nie istnieje", accessToken)).build();
        } else if (!visit.getStatus().equals(VisitStatus.ACCEPTED)) {
            return Response.status(400).entity(new ErrorResponse("Wizyta została zaakceptowana. Nie można jej usunąć", accessToken)).build();
        }
        visitsRepository.removeVisit(visit);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
    }

    @POST
    @Path("/getAllClientsVisits")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClientsVisits(GetAllUserVisitsForm form) {
        try {
            Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
            if (client == null) {
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
            }
            String accessToken = clientsRepository.generateToken(client);
            Set<Visit> visits = new HashSet<>();
            for (CarsHasOwners cho : client.getCars()) {
                visits.addAll(cho.getCar().getVisits());
            }
            VisitResponseModel[] visitsArray = visitsListToArray(visits);
            return Response.status(200).entity(new GetAllVisitsResponse(accessToken, visitsArray)).build();
        } catch (Exception e) {
            return Response.status(500).entity(new ErrorResponse("Wystąpił nieoczekiwany błąd przepraszamy", null)).build();
        }
    }

    public VisitResponseModel[] visitsListToArray(Collection<Visit> visitsList) {
        VisitResponseModel[] visits = new VisitResponseModel[visitsList.size()];
        int i = 0;
        for (Visit visit : visitsList) {
            visits[i] = new VisitResponseModel(visit);
            i++;
        }
        return visits;
    }

    @GET
    @Path("/getAllCarVisits")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCarVisits(GetAllCarVisitsForm form) {
        Car car = carsRepository.getCarByVin(form.getVin());
        if (car == null) {
            return Response.status(400).entity(new ErrorResponse("Brak samochodów o podanym numerze VIN", null)).build();
        }
        VisitResponseModel[] visits = visitsListToArray(car.getVisits());
        return Response.status(200).entity(new GetAllVisitsResponse(null, visits)).build();
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
        String token = employeesRepository.generateToken(employee);
        visitsRepository.insertService(new Service(form.getServiceName(), form.getTax()));
        return Response.status(200).entity(new PositiveResponse(token)).build();
    }
    @POST
    @Path("/getAllEmployeeVisits")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmployeeVisits(GetAllUserVisitsForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
        }
        VisitResponseModel[] visits = new VisitResponseModel[employee.getVisits().size()];
        int i = 0;
        for(Visit visit: employee.getVisits()){
            visits[i] = new VisitResponseModel(visit);
            i++;
        }
        return Response.status(200).entity(new GetAllVisitsResponse(form.getAccessToken(), visits)).build();
    }

    @POST
    @Path("/getSingleVisitDetails")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
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
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataForVisit(){
        List<CarPart> parts = carPartsRepository.getAllCarParts();
        CarPartModel[] carParts = new CarPartModel[parts.size()];
        int i = 0;
        for(CarPart currentPart : parts){
            carParts[i] = new CarPartModel(currentPart.getName());
            i++;
        }
        List<Service> services = servicesRepository.getAllServices();
        ServiceModel[] servicesArray = new ServiceModel[services.size()];
        i = 0;
        for(Service currentService : services){
            servicesArray[i] = new ServiceModel(currentService.getName()
            );
            i++;
        }
        return Response.status(200).entity(new GetStuffForVisitsResponse(carParts, servicesArray)).build();
    }
}
