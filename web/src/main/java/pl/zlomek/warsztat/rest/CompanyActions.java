package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.CarServiceDataRespository;
import pl.zlomek.warsztat.data.CompaniesRepository;
import pl.zlomek.warsztat.data.CompanyDataRespository;
import pl.zlomek.warsztat.data.EmployeesRepository;
import pl.zlomek.warsztat.model.*;

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
    CompanyDataRespository companyDataRespository;

    @Inject
    CarServiceDataRespository carServiceDataRespository;

    @Inject
    EmployeesRepository employeesRepository;

    Logger log = LoggerFactory.getLogger(CompanyActions.class);

    @POST
    @Path("/addCompany")
    @Transactional
    public Response addCompany(AddCompanyForm form){
        Company company = companiesRepository.getCompanyByName(form.getName());
        if(company != null){
            return Response.status(400).entity(new ErrorResponse("Podana firma istnieje")).build();
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
    @Transactional
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
    @Transactional
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

    @POST
    @Path("/editCompany")
    @Transactional
    public Response editCompany(EditCompanyForm form){
        Employee employee = employeesRepository.findByToken(form.getAccessToken());
        if(employee == null){
            return Response.status(401).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        Company company = companiesRepository.getCompanyByName(form.getCurrentName());
        if (company == null){
            return Response.status(400).entity(accessToken).build();
        }
        setCompanyFields(company,form);
        companiesRepository.updateCompany(company);
        return Response.status(200).entity(accessToken).build();
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
}
