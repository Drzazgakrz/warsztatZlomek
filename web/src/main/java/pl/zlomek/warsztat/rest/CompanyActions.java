package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.CarServiceDataRespository;
import pl.zlomek.warsztat.data.CompaniesRepository;
import pl.zlomek.warsztat.data.CompanyDataRespository;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/companies")
public class CompanyActions {
    @Inject
    CompaniesRepository companiesRepository;

    @Inject
    CompanyDataRespository companyDataRespository;

    @Inject
    CarServiceDataRespository carServiceDataRespository;

    Logger log = LoggerFactory.getLogger(CompanyActions.class);

    @POST
    @Path("/addCompany")
    public Response addCompany(AddCompanyForm form){
        Company company = companiesRepository.getCompanyByName(form.getName());
        if(company != null){
            log.info("nie istnieje");
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

    @POST
    @Path("/addCompanyData")
    public Response addCompanyData(AddCompanyDataForm form){
        CompanyData companyData =  companyDataRespository.getCompanyDataByName(form.getName());
        if(companyData != null){
            return Response.status(400).entity(new ErrorResponse("Podana firma istnieje juz w bazie")).build();
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
        return Response.status(200).build();
    }

    @POST
    @Path("/addCarServiceData")
    public Response addCarServiceData(AddCarServiceDataForm form){
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
        return Response.status(200).build();
    }
}
