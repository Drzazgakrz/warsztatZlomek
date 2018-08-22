package pl.zlomek.warsztat.rest;

import pl.zlomek.warsztat.data.CarRepository;
import pl.zlomek.warsztat.data.ClientsRepository;
import pl.zlomek.warsztat.model.Car;
import pl.zlomek.warsztat.model.CarDataForm;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/updateClient")
public class UpdateClient {

    @Inject
    private CarRepository carRepository;

    @Inject
    private ClientsRepository clientsRepository;

    @POST
    @Path("/addCar")
    public Response addCar(CarDataForm carData){
        Car car = carRepository.getCar(carData.getVin());
        if(car==null){
            car = new Car(/*Uzupełnij konstruktor*/);
            carRepository.insertCar(car);
        }

        //clientsRepository.findClient(carData)

        return Response.status(200).build();
    }
}
