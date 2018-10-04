package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.CompaniesRepository;
import pl.zlomek.warsztat.data.EmployeesRepository;
import pl.zlomek.warsztat.model.AddCompanyForm;
import pl.zlomek.warsztat.model.Company;
import pl.zlomek.warsztat.model.Employee;
import pl.zlomek.warsztat.model.ErrorResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/companies")
public class CompanyActions {
    @Inject
    CompaniesRepository companiesRepository;

    @Inject
    EmployeesRepository employeesRepository;

    Logger log = LoggerFactory.getLogger(CompanyActions.class);
/*new ErrorResponse("Podana funkcja istnieje")*/
    @POST
    @Path("/addCompany")
    @Transactional
    public Response addCompany(AddCompanyForm form){
        Employee employee = employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        Company company = companiesRepository.getCompanyByName(form.getName());
        if(company != null){
            return Response.status(400).entity(accessToken).build();
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
        return Response.status(200).entity(accessToken).build();
    }
}
