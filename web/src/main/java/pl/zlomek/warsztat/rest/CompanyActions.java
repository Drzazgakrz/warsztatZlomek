package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.CompaniesRepository;
import pl.zlomek.warsztat.model.AddCompanyForm;
import pl.zlomek.warsztat.model.Company;
import pl.zlomek.warsztat.model.ErrorResponse;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/companies")
public class CompanyActions {
    @Inject
    CompaniesRepository companiesRepository;

    Logger log = LoggerFactory.getLogger(CompanyActions.class);

    @POST
    @Path("/addCompany")
    public Response addCompany(AddCompanyForm form){
        Company company = companiesRepository.getCompanyByName(form.getName());
        if(company != null){
            return Response.status(400).entity(new ErrorResponse("Podana funkcja istnieje")).build();
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
        return Response.status(200).build();
    }
}
