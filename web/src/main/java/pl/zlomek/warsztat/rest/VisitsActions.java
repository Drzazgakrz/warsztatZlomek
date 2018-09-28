package pl.zlomek.warsztat.rest;


import pl.zlomek.warsztat.data.*;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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


    @POST
    @Transactional
    @Path("/submit")
    public Response submitVisit(SubmitVisitForm form){
        try {
            Employee employee = employeesRepository.findByToken(form.getAccessToken());
            if (employee == null) {
                return Response.status(403).build();
            }
            String accessToken = employeesRepository.generateToken(employee);
            Visit visit = visitsRepository.getVisitById(form.getVisitId());
            if (visit == null) {
                return Response.status(400).entity(accessToken).build();
            }
            Overview overview = visit.getOverview();
            if (overview != null && form.getCountYears() != null) {
                overview.addTerminateOverview(form.getCountYears());
            } else if (overview != null && form.getCountYears() == null) {
                return Response.status(400).entity(accessToken).build();
            }
            if(visit.getStatus()!= VisitStatus.IN_PROGRESS){
                return Response.status(400).entity(accessToken).build();
            }
            visit.setStatus(VisitStatus.FINISHED);
            List<CarPartModel> carPartModelList = Arrays.asList(form.getCarParts());
            carPartModelList.forEach(carPartModel -> {
                CarPart carPart = carPartsRepository.getCarPartByName(carPartModel.getName());
                if (carPart == null) {
                    return;
                }
                VisitsParts part = new VisitsParts(visit, carPart, carPartModel.getCount(), carPartModel.getPrice());
                visitsRepository.createVisitPart(part);
                visit.getParts().add(part);
                carPart.addVisit(part);
                carPartsRepository.updateCarPart(carPart);
            });
            visitsRepository.updateVisit(visit);
            return Response.status(200).entity(accessToken).build();
        }catch (Exception e){
            return Response.status(419).build();
        }
    }

    @POST
    @Transactional
    @Path("/addEmployee")
    public Response addEmployee(AddEmployeeForm form){

        Employee employee = employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(403).build();
        }
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if(visit == null || !visit.getStatus().equals(VisitStatus.ACCEPTED)){
            String accessToken = employeesRepository.generateToken(employee);
            return Response.status(400).entity(accessToken).build();
        }
        visit.setStatus(VisitStatus.IN_PROGRESS);
        visit.setEmployee(employee);
        visitsRepository.updateVisit(visit);
        employee.getVisits().add(visit);
        employeesRepository.update(employee);
        String accessToken = employeesRepository.generateToken(employee);
        return Response.status(200).entity(accessToken).build();
    }

    @POST
    @Transactional
    @Path("/add")
    public Response addVisit(CreateVisitForm form){
        Client client = clientsRepository.findByToken(form.getAccessToken());
        if (client == null)
            return Response.status(403).build();
        Car car = carsRepository.getCarById(form.getCarId());
        String accessToken = clientsRepository.generateToken(client);
        if(car == null) {
            return Response.status(400).entity(accessToken).build();
        }
        if(!client.checkCar(car)){
            return Response.status(401).entity(accessToken).build();
        }
        Overview overview = null;
        if(form.isOverview()){
            overview = new Overview(form.getVisitDate(), car);
            visitsRepository.createOverview(overview);
        }
        Visit visit = new Visit(form.getVisitDate(), car, overview);
        visitsRepository.createVisit(visit);
        return Response.status(200).entity(accessToken).build();
    }

    @POST
    @Path("/removeVisit")
    @Transactional
    public Response removeVisit(ReomoveVisitForm form){
        Client client = clientsRepository.findByToken(form.getAccessToken());
        if(client == null){
            return Response.status(403).build();
        }
        String accessToken = clientsRepository.generateToken(client);
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if(visit == null){
            return Response.status(400).entity(accessToken).build();
        }else if(!visit.getStatus().equals(VisitStatus.ACCEPTED)){
            return Response.status(400).entity(accessToken).build();
        }
        visitsRepository.removeVisit(visit);
        return Response.status(200).entity(accessToken).build();
    }
}
