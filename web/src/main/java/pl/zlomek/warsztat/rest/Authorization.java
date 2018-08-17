package pl.zlomek.warsztat.rest;

import pl.zlomek.warsztat.data.ClientsRepository;
import pl.zlomek.warsztat.model.Client;
import pl.zlomek.warsztat.model.RegisterForm;
import pl.zlomek.warsztat.model.SignInForm;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/authorization")
public class Authorization {

    private Logger log = LoggerFactory.getLogger(Authorization.class);

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

    @POST
    @Path("/signIn")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signIn(SignInForm signInForm){
        if(signInForm.getPassword()!= null || signInForm.getUsername()!=null){
            Client client = repository.findClient(signInForm.getUsername(), signInForm.getPassword());
            if(client==null){
                return Response.status(403).build();
            }
            log.info(client.toString());
            return Response.status(200).build();
        }
        return Response.status(403).build();
    }
}