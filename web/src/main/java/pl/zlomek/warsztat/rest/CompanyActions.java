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
        String accessToken = employeesRepository.generateToken(employee);
        Company company = companiesRepository.getCompanyByName(form.getName());
        if(company != null){
            return Response.status(400).entity(new ErrorResponse("Podana firma istnieje",accessToken)).build();
        }
        String companyName = form.getName();
        String nip = form.getNip();
        String cityName = form.getCityName();
        String streetName= form.getStreetName();
        String aptName = form.getAptNum();
        String zipCode = form.getZipCode();
        String email = form.getEmail();
        String buildNum = form.getBuildingNum();
        company = new Company(nip,email,companyName,cityName,streetName, buildNum, aptName, zipCode);
        companiesRepository.insert(company);
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
    }
    @POST
    @Path("/addCompanyData")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCompanyData(AddCompanyDataForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null){
            return Response.status(401).entity(new ErrorResponse("Nie udało się zalogować", null)).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        CompanyData companyData =  companyDataRespository.getCompanyDataByName(form.getName());
        if(companyData != null){
            return Response.status(400).entity(new ErrorResponse("Podana firma istnieje juz w bazie", accessToken)).build();
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
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
    }
    @POST
    @Path("/addCarServiceData")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCarServiceData(AddCarServiceDataForm form){
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null){
            return Response.status(401).entity(new ErrorResponse("Nie udało się zalogować", null)).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        String serviceName = form.getServiceName();
        String nip = form.getNip();
        String cityName = form.getCityName();
        String streetName= form.getStreetName();
        String aptNum = form.getAptNum();
        String zipCode = form.getZipCode();
        String email = form.getEmail();
        String buildNum = form.getBuildingNum();
        CarServiceData carServiceData = new CarServiceData(nip, email, serviceName, cityName, streetName, buildNum, aptNum, zipCode);
        carServiceDataRespository.insert(carServiceData);
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
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
        String accessToken = employeesRepository.generateToken(employee);
        Company company = companiesRepository.getCompanyByName(form.getCurrentName());
        if (company == null){
            return Response.status(400).entity(new ErrorResponse("Brak firmy o podanej nazwie", accessToken)).build();
        }
        setCompanyFields(company,form);
        companiesRepository.updateCompany(company);
        return Response.status(200).entity(new AccessTokenForm((accessToken))).build();
    }
    public void setCompanyFields(Company company, EditCompanyForm form){
        if(form.getAptNum()!= null)
            company.setAptNum(form.getAptNum());
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
        Company company = companiesRepository.getCompanyByName(form.getCompanyName());
        if(company == null){
            return Response.status(400).entity(new ErrorResponse("Brak podanej firmy", form.getAccessToken())).build();
        }
        return Response.status(200).entity(new CompanyInfoResponse(form.getAccessToken(), company)).build();

    }
}