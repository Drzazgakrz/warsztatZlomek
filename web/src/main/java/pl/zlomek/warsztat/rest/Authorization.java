package pl.zlomek.warsztat.rest;

import pl.zlomek.warsztat.data.ClientsRepository;
import pl.zlomek.warsztat.model.Client;
import pl.zlomek.warsztat.model.RegisterForm;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/authorization")
public class Authorization {

    @Inject
    private ClientsRepository repository;

    //ścieżka localhost:8080/warsztatZlomek/rest/authorization/register
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterForm newUserData){

        if(newUserData.getPassword().equals(newUserData.getConfirmPassword())) {
            String firstName = newUserData.getFirstName();
            String lastName = newUserData.getLastName();
            String email = newUserData.getEmail();
            String phoneNum = newUserData.getPhoneNumber();
            String cityName = newUserData.getCityName();
            String streetName = newUserData.getStreetName();
            String buildNum = newUserData.getBuildNum();
            String aptNum = newUserData.getAptNum();
            String zipCode = newUserData.getZipCode();
            String password = newUserData.getPassword();
            Client client = new Client(firstName,lastName,email,phoneNum, cityName,streetName,buildNum,aptNum, zipCode,password,null,null);
            repository.registerUser(client);
            return Response.ok().build();
        } return Response.status(400).build();
    }
}
