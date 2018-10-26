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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

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

    @Inject
    private VisitsRepository visitsRepository;

    private Logger log = LoggerFactory.getLogger(InvoiceActions.class);

    BigDecimal sumA = new BigDecimal(0);
    BigDecimal sumB = new BigDecimal(0);
    BigDecimal sumC = new BigDecimal(0);
    BigDecimal sumD = new BigDecimal(0);

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
        Visit visit = visitsRepository.getVisitById(newInvoice.getVisitId());

        if (invoice != null && visit != null) {
            invoicesRepository.insertInvoice(invoice);
            visit.getParts().forEach((position)->{
                CarPart part = position.getPart();
                InvoicePosition invoicePosition = new InvoicePosition(position, part.getName(), part.getTax(), invoice, "szt.");
                invoice.getInvoicePositions().add(invoicePosition);
                invoicesRepository.insertInvoicePosition(invoicePosition);
            });
            visit.getServices().forEach((position)->{
                Service service = position.getService();
                InvoicePosition invoicePosition = new InvoicePosition(position, service.getName(),service.getTax() , invoice, "h");
                invoice.getInvoicePositions().add(invoicePosition);
                invoicesRepository.insertInvoicePosition(invoicePosition);
            });
            invoicesRepository.updateInvoice(invoice);
            return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
        } else
            return Response.status(500).entity(new ErrorResponse("Nie udało się utworzyć faktury", accessToken)).build();
    }

    public void addValue(BigDecimal value, int tax){
        switch (tax){
            case 23:
                sumA = sumA.add(value);
                break;
            case 18:
                sumB = sumB.add(value);
                break;
            case 5:
                sumC = sumC.add(value);
                break;
        }
    }

    public MethodOfPayment createMethodOfPayment(String method) {
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

            MethodOfPayment methodOfPayment = createMethodOfPayment(newInvoice.getMethodOfPayment());

            CarServiceData carServiceData = carServiceDataRespository.getTopServiceData();

            if (carServiceData != null && companyData != null && methodOfPayment != null) {
                Invoice invoice = new Invoice(discount, methodOfPayment, companyData, carServiceData);
                StringBuilder invoiceNumberBuilder = new StringBuilder().append(invoicesRepository.countInvoicesInMonth());
                String invoiceNumber = invoiceNumberBuilder.append("/").append(LocalDate.now().getMonthValue()).append("/").append(LocalDate.now().getYear()).toString();
                invoice.setInvoiceNumber(invoiceNumber);
                return invoice;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        return Response.status(200).entity(new AccessTokenForm(accessToken)).build();
    }
}
