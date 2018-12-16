package pl.zlomek.warsztat.rest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.*;
import pl.zlomek.warsztat.model.*;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/companies")
public class CompanyActions {
    @Inject
    CompaniesRepository companiesRepository;
    @Inject
    CompanyDataRespository companyDataRespository;
    @Inject
    CarServiceDataRespository carServiceDataRespository;
    @Inject
    EmployeesRepository employeesRepository;

    Logger log = LoggerFactory.getLogger(CompanyActions.class);

    @Inject
    ClientsRepository clientsRepository;

    @POST
    @Path("/addCompany")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCompany(CompanyForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null){
            return Response.status(401).entity(new ErrorResponse("Nie udało się zalogować", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Company company = companiesRepository.getCompanyByName(form.getName());
        if(company != null){
            return Response.status(400).entity(new ErrorResponse("Podana firma istnieje",form.getAccessToken())).build();
        }
        company = new Company(form.getNip(),form.getEmail(),form.getName(),form.getCityName(),form.getStreetName(),
                form.getBuildingNum(), form.getAptNum(), form.getZipCode());
        companiesRepository.insert(company);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }
    @POST
    @Path("/addCompanyData")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCompanyData(CompanyDataForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null){
            return Response.status(401).entity(new ErrorResponse("Nie udało się zalogować", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        CompanyData companyData =  companyDataRespository.getCompanyDataByName(form.getName());
        if(companyData != null){
            return Response.status(400).entity(new ErrorResponse("Podana firma istnieje juz w bazie", form.getAccessToken())).build();
        }
        String companyName = form.getName();
        String nip = form.getNip();
        String cityName = form.getCityName();
        String streetName= form.getStreetName();
        String aptNum = form.getAptNum();
        String zipCode = form.getZipCode();
        String buildNum = form.getBuildingNum();
        companyData = new CompanyData(nip, companyName, cityName, streetName, buildNum, aptNum, zipCode);
        companyDataRespository.insert(companyData);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }
    @POST
    @Path("/addCarServiceData")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCarServiceData(CompanyForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null){
            return Response.status(401).entity(new ErrorResponse("Nie udało się zalogować", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        String serviceName = form.getName();
        String nip = form.getNip();
        String cityName = form.getCityName();
        String streetName= form.getStreetName();
        String aptNum = form.getAptNum();
        String zipCode = form.getZipCode();
        String email = form.getEmail();
        String buildNum = form.getBuildingNum();
        CarServiceData carServiceData = new CarServiceData(nip, email, serviceName, cityName, streetName, buildNum, aptNum, zipCode);
        carServiceDataRespository.insert(carServiceData);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }
    @POST
    @Path("/editCompany")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response editCompany(EditCompanyForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Nie udało się zalogować", null)).build();
        }
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        log.info(Long.toString(form.getId()));
        Company company = companiesRepository.getCompanyById(form.getId());
        if (company == null){
            return Response.status(400).entity(new ErrorResponse("Brak firmy o podanej nazwie", form.getAccessToken())).build();
        }
        setCompanyFields(company,form);
        companiesRepository.updateCompany(company);
        return Response.status(200).entity(new AccessTokenForm((form.getAccessToken()))).build();
    }
    public void setCompanyFields(Company company, EditCompanyForm form){
        if(form.getAptNum()!= null)
            company.setAptNum((form.getAptNum().equals("")?null:form.getAptNum()));
        if(form.getBuildingNum()!= null)
            company.setBuildingNum(form.getBuildingNum());
        if(form.getCityName()!= null)
            company.setCityName(form.getCityName());
        if(form.getEmail()!= null)
            company.setEmail(form.getEmail());
        if(form.getName()!= null)
            company.setCompanyName(form.getName());
        if(form.getZipCode() != null)
            company.setZipCode(form.getZipCode());
        if(form.getStreetName()!= null)
            company.setStreetName(form.getStreetName());
    }

    @POST
    @Path("/getCompanyData")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanyData(GetCompanyForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        Company company = companiesRepository.getCompanyById(form.getCompanyId());
        if(company == null){
            return Response.status(400).entity(new ErrorResponse("Brak podanej firmy", form.getAccessToken())).build();
        }
        return Response.status(200).entity(new CompanyInfoResponse(form.getAccessToken(), company)).build();

    }

    @POST
    @Path("/getClientsCompanies")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientsCompanies(AccessTokenForm form){
        Client client = (Client) clientsRepository.findByToken(form.getAccessToken());
        if(client == null || !client.getStatus().equals(ClientStatus.ACTIVE)){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        List<CompaniesHasEmployees> clientsCompanies = client.getCompanies().stream().filter(che->che.getStatus().
                equals(EmploymentStatus.CURRENT_EMPLOYER)).collect(Collectors.toList());
        CompanyInfoResponse[] companies = new CompanyInfoResponse[clientsCompanies.size()];
        int i = 0;
        for(CompaniesHasEmployees che: clientsCompanies){
            companies[i] = new CompanyInfoResponse(null, che.getCompany());
            i++;
        }
        log.info(Integer.toString(companies.length)+" "+Integer.toString(i));
        return Response.status(200).entity(new ClientsCompaniesResponse(form.getAccessToken(), companies)).build();
    }

    @POST
    @Path("/getAllCompanies")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanies(AccessTokenForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        List<Company> companies = companiesRepository.getAllCompanies();
        CompanyInfoResponse[] companiesArray = new CompanyInfoResponse[companies.size()];
        int i = 0;
        for(Company company: companies){
            companiesArray[i] = new CompanyInfoResponse(null, company);
            i++;
        }
        return Response.status(200).entity(new ClientsCompaniesResponse(form.getAccessToken(), companiesArray)).build();
    }
}