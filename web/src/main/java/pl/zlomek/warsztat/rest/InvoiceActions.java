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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;

@Path("/invoice")
public class InvoiceActions {

    @Inject
    private InvoicesRepository invoicesRepository;

    @Inject
    private EmployeesRepository employeesRepository;

    @Inject
    private CompanyDataRespository companyDataRespository;

    @Inject
    private CompaniesRepository companiesRepository;

    @Inject
    CarServiceDataRespository carServiceDataRespository;

    private Logger log = LoggerFactory.getLogger(InvoiceActions.class);

    @POST
    @Path("/addInvoice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addInvoice(AddInvoiceForm newInvoice) {
        Employee employee = (Employee) employeesRepository.findByToken(newInvoice.getAccessToken());
        if (employee == null)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        String accessToken = employeesRepository.generateToken(employee);
        Company company = companiesRepository.getCompanyByName(newInvoice.getCompanyName());
        CompanyData companyData = new CompanyData(company);
        companyDataRespository.insert(companyData);
        Invoice invoice = createInvoice(newInvoice, companyData);
        if (invoice != null) {
            invoicesRepository.insertInvoice(invoice);
            return Response.status(200).entity(new PositiveResponse(accessToken)).build();
        } else
            return Response.status(500).entity(new ErrorResponse("Nie udało się utworzyć faktury",accessToken)).build();
    }

    public MethodOfPayment createMethodOfPayment(String method){
        switch (method) {
            case ("CASH"):
                return MethodOfPayment.CASH;
            case ("CARD"):
                return MethodOfPayment.CARD;
            case ("TRANSFER"):
                return MethodOfPayment.TRANSFER;
        }
        return null;
    }

    public Invoice createInvoice(AddInvoiceForm newInvoice, CompanyData companyData) {
        try {
            int discount = newInvoice.getDiscount();
            int tax = newInvoice.getTax();

            MethodOfPayment methodOfPayment = createMethodOfPayment(newInvoice.getMethodOfPayment());

            BigDecimal netValue = new BigDecimal(newInvoice.getNetValue());
            BigDecimal grossValue = new BigDecimal(newInvoice.getGrossValue());
            BigDecimal valueOfVat = new BigDecimal(newInvoice.getValueOfVat());
            CarServiceData carServiceData = carServiceDataRespository.getTopServiceData();

            if (carServiceData != null && companyData != null && methodOfPayment != null) {

                Invoice invoice = new Invoice(discount, tax, methodOfPayment, netValue, grossValue, valueOfVat, companyData, carServiceData);
                StringBuilder invoiceNumberBuilder = new StringBuilder().append(invoicesRepository.countInvoicesInMonth());
                String invoiceNumber = invoiceNumberBuilder.append("/").append(GregorianCalendar.MONTH).append("/").append(GregorianCalendar.YEAR).toString();
                invoice.setInvoiceNumber(invoiceNumber);
                return invoice;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @POST
    @Path("/editInvoice")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response editInvoice(EditInvoiceForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null) {
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        }
        String accessToken = employeesRepository.generateToken(employee);
        Invoice invoice = invoicesRepository.getInvoiceById(form.getInvoiceId());

        if (invoice == null) {
            return Response.status(400).entity(new ErrorResponse("Brak faktury", accessToken)).build();
        }

        Invoice newInvoice = createInvoice(form, invoice.getCompanyData());
        if (newInvoice == null) {
            return Response.status(400).entity(new ErrorResponse("Nie utworzono nowej faktury", accessToken)).build();
        }
        invoicesRepository.insertInvoice(newInvoice);
        invoice.setCorectionInvoice(newInvoice);
        invoicesRepository.updateInvoice(invoice);
        return Response.status(200).entity(new PositiveResponse(accessToken)).build();
    }
}
