package pl.zlomek.warsztat.rest;


import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.*;
import pl.zlomek.warsztat.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
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
import pl.zlomek.warsztat.model.Service;


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
            if (!form.validate()) {
                return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
            }
            Visit visit = visitsRepository.getVisitById(form.getVisitId());
            if (visit == null)
                return Response.status(400).entity(new ErrorResponse("Brak wizyty o podanym ID", form.getAccessToken())).build();

            Response response = setVisitsOverview(visit, form);
            if (response != null)
                return response;
            VisitStatus status = getVisitStatus(form.getStatus());

            if (status != null && !visit.getStatus().equals(status))
                this.setVisitsStatus(visit, status);

            if (form.getCarParts() != null)
                addCarParts(visit, form);

            if (form.getServices() != null)
                this.addServices(visit, form);

            visitsRepository.updateVisit(visit);
            return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Wystąpił nieznany błąd. Przepraszamy.").build();
        }
    }

    public Response setVisitsOverview(Visit visit, SubmitVisitForm form) {
        Overview overview = visit.getOverview();
        if (overview != null && form.getCountYears() != null) {
            Car car = visit.getCar();
            car.getOverviews().add(overview);
            carsRepository.updateCar(car);
            overview.addTerminateOverview(form.getCountYears());
        } else if (overview != null && form.getCountYears() == null &&
                (form.getStatus().equals(VisitStatus.FOR_PICKUP.toString()) ||
                        form.getStatus().equals(VisitStatus.FINISHED.toString()))) {
            return Response.status(400).entity(new ErrorResponse(
                    "Przegląd powinien mieć termin ważności", form.getAccessToken())).build();
        }
        return null;
    }

    public void addCarParts(Visit visit, SubmitVisitForm form){
        List<CarPartModel> carPartModelList = Arrays.asList(form.getCarParts());
        carPartModelList.forEach(carPartModel -> {
            CarPart carPart = carPartsRepository.getCarPartById(carPartModel.getId());
            if (carPart == null) {
                return;
            }
            Object[] parts = visit.getParts().stream().filter((part) -> carPart.equals(part.getPart())).toArray();
            if (parts.length != 0) {
                VisitsParts part = (VisitsParts) parts[0];
                part.setCount(carPartModel.getCount());
                part.setSinglePrice(carPartModel.getPrice());
                carPartsRepository.updateVisitsParts(part);
                return;
            }
            VisitsParts relation = visit.addPartToVisit(carPart, carPartModel.getCount(), carPartModel.getPrice());
            visitsRepository.createVisitPart(relation);
            carPartsRepository.updateCarPart(carPart);
        });
    }

    public void addServices(Visit visit, SubmitVisitForm form){
        List<ServiceModel> carPartModelList = Arrays.asList(form.getServices());
        carPartModelList.forEach(serviceModel -> {
            Service service = servicesRepository.getServiceByName(serviceModel.getName());
            if (service == null) {
                return;
            }
            Object[] parts = visit.getServices().stream().filter((currentService) ->
                    service.equals(currentService.getService())).toArray();
            if (parts.length != 0) {
                VisitsHasServices currentService = (VisitsHasServices) parts[0];
                currentService.setCount(serviceModel.getCount());
                currentService.setSinglePrice(new BigDecimal(serviceModel.getPrice()));
                servicesRepository.updateVisitsService(currentService);
                return;
            }
            VisitsHasServices relation = visit.addServiceToVisit(service, serviceModel.getCount(), new BigDecimal(serviceModel.getPrice()));
            servicesRepository.insertVisitsServices(relation);
            servicesRepository.updateService(service);
        });
    }

    public void setVisitsStatus(Visit visit, VisitStatus status) {
        if (visit.getStatus().equals(VisitStatus.IN_PROGRESS) && status.equals(VisitStatus.FOR_PICKUP)) {
            sendMail(visit, createMessage(visit, status));
            visit.setVisitFinished(LocalDate.now());
        } else if (visit.getStatus().equals(VisitStatus.NEW) && status.equals(VisitStatus.ACCEPTED)) {
            sendMail(visit, createMessage(visit, status));
        }
        visit.setStatus(status);
    }

    public MessageModel createMessage(Visit visit, VisitStatus status){
        String subject, message;
        String registrationNumber = carsRepository.getOwnership(visit.getCar().getId(), visit.getClient().getClientId()).
                getRegistrationNumber();
        LocalDate localDate = visit.getVisitDate().toLocalDate();
        String date = localDate.getDayOfMonth() + "-" + localDate.getMonthValue() + "-" + localDate.getYear();
        switch (status){
            case ACCEPTED:
                subject = "Potwierdzenie wizyty";
                message = "Akceptacja wizyty umówionej na " + date + " dla samochodu o numerze rejestracyjnym "
                        + registrationNumber + ". \nZespół Warsztat Złomek";
                return new MessageModel(message, subject);
            case FOR_PICKUP:
                subject = "Zakończenie wizyty";
                message = "Samochód o numerze rejestracyjnym " + registrationNumber +
                        " jest już do odbioru. \nZespół Warsztat Złomek";
                return new MessageModel(message,subject);
            case NEW:
                subject = "Rezerwacja wizyty";
                message = "Wizyta została zarezerwowana dla samochodu o numerze rejestracyjnym " + registrationNumber +
                        " na " + date + ". \nZespół Warsztat Złomek";
                return new MessageModel(message, subject);
        }
        return null;
    }

    public void sendMail(Visit visit, MessageModel messageModel) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "poczta.o2.pl");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    "warsztat_zlomek@o2.pl", "abc123*%*3");
                        }
                    });
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("warsztat_zlomek@o2.pl"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(visit.getClient().getEmail()));
            message.setSubject(messageModel.getSubject(), "UTF-8");
            message.setText(messageModel.getMessage(), "UTF-8");

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
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
        if (!form.validate()) {
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (visit == null || !visit.getStatus().equals(VisitStatus.NEW)) {
            return Response.status(400).entity(new ErrorResponse("Wizyta nie istnieje lub zostałą wybrana przez innego pracownika", form.getAccessToken())).build();
        }

        this.setVisitsStatus(visit, VisitStatus.ACCEPTED);
        visit.setEmployee(employee);
        employee.getVisits().add(visit);
        visitsRepository.updateVisit(visit);
        employeesRepository.update(employee);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    public Overview creatOberview(CreateVisitForm form, LocalDateTime visitDate, Car car){
        Overview overview = null;
        if (form.isOverview()) {
            overview = new Overview(visitDate, car);
            visitsRepository.createOverview(overview);
        }
        return overview;
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
        if (car == null || client.checkCar(car, client).length < 1) {
            return Response.status(400).entity(new ErrorResponse("Podany samochód nie istnieje lub nie należy do tego klienta",
                    form.getAccessToken())).build();
        }
        LocalDateTime visitDate = form.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(1);
        if (LocalDateTime.now().isAfter(visitDate) || LocalDateTime.now().plusDays(21).isBefore(visitDate))
            return Response.status(400).entity(new ErrorResponse(
                    "Data wizyty musi być późniejsza niż dzisiejsza data i nie może być zaplanowana na dalej niż 3 tygodnie",
                    form.getAccessToken())).build();
        Overview overview = creatOberview(form, visitDate, car);
        Visit visit = new Visit(visitDate, car, overview, client);
        car.getVisits().add(visit);
        carsRepository.updateCar(car);
        visitsRepository.createVisit(visit);

        sendMail(visit, createMessage(visit, VisitStatus.NEW));
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
        if (!form.validate()) {
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (visit == null) {
            return Response.status(400).entity(new ErrorResponse("Wizyta o podanym id nie istnieje", form.getAccessToken())).build();
        } else if (!visit.getStatus().equals(VisitStatus.NEW)) {
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
                    LocalDate end = (cho.getEndOwnershipDate() != null) ? cho.getEndOwnershipDate() : LocalDate.now();
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
        if (!form.validate()) {
            return Response.status(400).entity(new ErrorResponse("Błędne dane", null)).build();
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
        if (!form.validate()) {
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        visitsRepository.insertService(new Service(form.getName(), form.getTax()));
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }


    @POST
    @Path("/getAllEmployeeVisits")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getAllEmployeeVisits(AccessTokenForm form) {
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
    public Response getSingleVisitDetails(GetSingleEmployeeVisitForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodłą się", null)).build();
        }
        Object[] visitsArray = employee.getVisits().stream().filter((visit -> visit.getId() == form.getVisitId())).toArray();
        if (visitsArray.length < 1) {
            return Response.status(403).entity(new ErrorResponse("Wizyta nie należy do tego pracownika", form.getAccessToken())).build();
        }
        VisitDetailsResponse visit = new VisitDetailsResponse((Visit) visitsArray[0]);
        return Response.status(200).entity(new GetSingleVisitDetails(form.getAccessToken(), visit)).build();
    }

    @GET
    @Path("/getDataForVisit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataForVisit() {
        List<CarPart> parts = carPartsRepository.getAllCarParts();
        CarPartModel[] carParts = new CarPartModel[parts.size()];
        int i = 0;
        for (CarPart currentPart : parts) {
            carParts[i] = new CarPartModel(currentPart);
            i++;
        }
        List<Service> services = servicesRepository.getAllServices();
        ServiceModel[] servicesArray = new ServiceModel[services.size()];
        i = 0;
        for (Service currentService : services) {
            servicesArray[i] = new ServiceModel(currentService);
            i++;
        }
        return Response.status(200).entity(new GetStuffForVisitsResponse(carParts, servicesArray)).build();
    }

    @POST
    @Path("/editService")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response editService(EditServiceForm form) {
        try {
            Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
            if (employee == null)
                return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
            if (!form.validate()) {
                return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
            }
            Service service = servicesRepository.getServiceById(form.getId());
            if (service == null)
                return Response.status(404).entity(new ErrorResponse("Brak podanej części", form.getAccessToken())).build();
            if (form.getName() != null && !service.getName().equals(form.getName())) {
                service.setName(form.getName());
            }
            if (form.getTax() != 0 && service.getTax() != form.getTax()) {
                service.setTax(form.getTax());
            }
            servicesRepository.updateService(service);
            return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
        } catch (Exception e) {
            return Response.status(500).entity(new ErrorResponse("Wystąpił błąd", form.getAccessToken())).build();
        }
    }

    @POST
    @Path("/getNewVisits")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getNewVisits(AccessTokenForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        VisitDetailsResponse[] visits = visitsListToArray(visitsRepository.getAllNewVisits());
        return Response.status(200).entity(new GetVisitsResponse(form.getAccessToken(), visits)).build();
    }

    @POST
    @Path("/getNotFinishedVisits")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotFinishedVisits(AccessTokenForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        List<Visit> notFinishedVisits = employee.getVisits().stream().filter(visit ->
                !visit.getStatus().equals(VisitStatus.NEW) && !visit.getStatus().equals(VisitStatus.FINISHED)).
                collect(Collectors.toList());
        VisitDetailsResponse[] visits = visitsListToArray(notFinishedVisits);
        return Response.status(200).entity(new GetVisitsResponse(form.getAccessToken(), visits)).build();
    }

    @POST
    @Path("/addEmptyVisit")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEmptyVisit(CreateVisitForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Client client = clientsRepository.getClientById(0L);
        Car car = carsRepository.getCarById(0L);
        if (client == null || car == null) {
            return Response.status(404).entity("Brak domyślnych danych").build();
        }
        LocalDateTime date = form.getVisitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Overview overview = creatOberview(form, date, car);
        Visit visit = new Visit(date, car, overview, client);
        visitsRepository.createVisit(visit);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }
}
