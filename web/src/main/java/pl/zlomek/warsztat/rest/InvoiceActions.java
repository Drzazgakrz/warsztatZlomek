package pl.zlomek.warsztat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.data.*;
import pl.zlomek.warsztat.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/invoice")
public class InvoiceActions {

    @Inject
    private InvoicesRepository invoicesRepository;

    @Inject
    private EmployeesRepository employeesRepository;

    @Inject
    private CompanyDataRespository companyDataRespository;

    @Inject
    CarServiceDataRespository carServiceDataRespository;

    private Logger log = LoggerFactory.getLogger(InvoiceActions.class);

    @POST
    @Path("/addInvoice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addInvoice(AddInvoiceForm newInvoice){
        Logger log = LoggerFactory.getLogger(InvoiceActions.class);
        Employee employee = employeesRepository.findByToken(newInvoice.getAccessToken());
        if(employee == null )
            return Response.status(403).build();
        String accessToken = employeesRepository.generateToken(employee);

        int discount = newInvoice.getDiscount();
        int tax = newInvoice.getTax();
        MethodOfPayment methodOfPayment = null;
        String method = newInvoice.getMethodOfPayment();
        switch (method){
            case("CASH") : methodOfPayment = MethodOfPayment.CASH; break;
            case("CARD") : methodOfPayment = MethodOfPayment.CARD; break;
            case("TRANSFER") : methodOfPayment = MethodOfPayment.TRANSFER; break;
        }

        BigDecimal netValue = newInvoice.getNetValue();
        BigDecimal grossValue = newInvoice.getGrossValue();
        BigDecimal valueOfVat = newInvoice.getValueOfVat();
        CarServiceData carServiceData = carServiceDataRespository.getTopServiceData();
        CompanyData companyData = companyDataRespository.getCompanyDataByName(newInvoice.getCompanyName());
        if(carServiceData != null && companyData != null && methodOfPayment != null){
            Invoice invoice = createInvoice(discount, tax, methodOfPayment, netValue, grossValue, valueOfVat, companyData, carServiceData);
            if(invoice != null)
            {
                invoicesRepository.insertInvoice(invoice);
                return Response.status(200).entity(accessToken).build();
            }
            else
                return Response.status(401).entity(accessToken).build();
        }
        else
            return Response.status(402).entity(accessToken).build();
    }

    public Invoice createInvoice(int discount, int tax, MethodOfPayment methodOfPayment, BigDecimal netValue, BigDecimal grossValue, BigDecimal valueOfVat, CompanyData companyData, CarServiceData carServiceData){
        try{
            Invoice invoice = new Invoice(discount, tax, methodOfPayment, netValue, grossValue, valueOfVat, companyData, carServiceData);
            invoice.setInvoiceNumber(invoice.getInvoiceNumber());
            return invoice;
        } catch (Exception e){
            log.info(e.toString());
            return null;
        }
    }
}
