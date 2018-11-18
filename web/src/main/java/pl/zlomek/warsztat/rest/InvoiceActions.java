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
import java.time.ZoneId;
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

    private BigDecimal sumA = new BigDecimal(0);
    private BigDecimal sumB = new BigDecimal(0);
    private BigDecimal sumC = new BigDecimal(0);
    private BigDecimal sumD = new BigDecimal(0);

    @POST
    @Path("/addInvoice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addInvoice(AddInvoiceForm newInvoice) {
        Employee employee = (Employee) employeesRepository.findByToken(newInvoice.getAccessToken());
        if (employee == null)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        if(!newInvoice.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", newInvoice.getAccessToken())).build();
        }

        Company company = companiesRepository.getCompanyByName(newInvoice.getCompanyName());
        if(company == null){
            return Response.status(400).entity(new ErrorResponse("Firma o podanej nazwie nie istnieje", newInvoice.getAccessToken())).build();
        }
        CompanyData companyData = new CompanyData(company);
        companyDataRespository.insert(companyData);
        Invoice invoice = createInvoice(newInvoice, companyData);
        if (invoice != null) {
            invoicesRepository.updateInvoice(invoice);
            return Response.status(200).entity(new InvoiceDetailsResponse(newInvoice.getAccessToken(), invoice)).build();
        } else
            return Response.status(500).entity(new ErrorResponse("Nie udało się utworzyć faktury", newInvoice.getAccessToken())).build();
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
            Visit visit = visitsRepository.getVisitById(newInvoice.getVisitId());
            if (carServiceData != null && companyData != null && methodOfPayment != null) {
                LocalDate paymentDate = newInvoice.getPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Invoice invoice = new Invoice(discount, methodOfPayment, companyData, carServiceData, paymentDate);
                StringBuilder invoiceNumberBuilder = new StringBuilder().append(invoicesRepository.countInvoicesInMonth());
                String invoiceNumber = invoiceNumberBuilder.append("/").append(LocalDate.now().getMonthValue()).append("/").append(LocalDate.now().getYear()).toString();
                invoice.setInvoiceNumber(invoiceNumber);
                invoicesRepository.insertInvoice(invoice);
                BigDecimal grossValue = new BigDecimal(0);
                BigDecimal netValue = new BigDecimal(0);
                for(VisitsParts position: visit.getParts()){
                    CarPart part = position.getPart();
                    InvoicePosition invoicePosition = new InvoicePosition(position, part.getName(), part.getTax(), invoice, "szt.");
                    invoice.getInvoicePositions().add(invoicePosition);
                    grossValue = grossValue.add(invoicePosition.getGrossPrice());
                    netValue = netValue.add(invoicePosition.getNetPrice());
                    invoicesRepository.insertInvoicePosition(invoicePosition);
                }
                for(VisitsHasServices position: visit.getServices()){
                    Service service = position.getService();
                    InvoicePosition invoicePosition = new InvoicePosition(position, service.getName(), service.getTax(), invoice, "h");
                    invoice.getInvoicePositions().add(invoicePosition);
                    grossValue = grossValue.add(invoicePosition.getGrossPrice());
                    netValue = netValue.add(invoicePosition.getNetPrice());
                    invoicesRepository.insertInvoicePosition(invoicePosition);
                }
                invoice.setNetValue(netValue);
                invoice.setGrossValue(grossValue);
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
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }
        Invoice invoice = invoicesRepository.getInvoiceById(form.getInvoiceId());

        if (invoice == null) {
            return Response.status(400).entity(new ErrorResponse("Brak faktury", form.getAccessToken())).build();
        }

        Invoice newInvoice = createInvoice(form, invoice.getCompanyData());
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (newInvoice == null || visit == null) {
            return Response.status(400).entity(new ErrorResponse("Nie utworzono nowej faktury", form.getAccessToken())).build();
        }
        invoicesRepository.insertInvoice(newInvoice);
        invoice.setCorectionInvoice(newInvoice);
        invoicesRepository.updateInvoice(invoice);
        return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
    }

    @POST
    @Path("/addProFormaInvoice")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProFormaInvoice(AddInvoiceForm form) {
        Employee employee = (Employee) employeesRepository.findByToken(form.getAccessToken());
        if (employee == null)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        if(!form.validate()){
            return Response.status(400).entity(new ErrorResponse("Błędne dane", form.getAccessToken())).build();
        }

        Company company = companiesRepository.getCompanyByName(form.getCompanyName());
        CompanyDataBuffer companyData = new CompanyDataBuffer(company);
        companiesRepository.insertComapnyDataBuffer(companyData);
        int discount = form.getDiscount();

        MethodOfPayment methodOfPayment = createMethodOfPayment(form.getMethodOfPayment());
        BigDecimal grossValue = new BigDecimal(0);
        BigDecimal netValue = new BigDecimal(0);
        CarServiceData carServiceData = carServiceDataRespository.getTopServiceData();
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (carServiceData != null && companyData != null && methodOfPayment != null) {
            LocalDate paymentDate = form.getPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            InvoiceBuffer invoice = new InvoiceBuffer(discount, methodOfPayment, companyData, carServiceData, paymentDate);
            StringBuilder invoiceNumberBuilder = new StringBuilder().append(invoicesRepository.countInvoicesInMonth());
            String invoiceNumber = invoiceNumberBuilder.append("/").append(LocalDate.now().getMonthValue()).append("/").append(LocalDate.now().getYear()).toString();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setInvoiceBufferStatus(InvoiceBufferStatus.proForma);
            invoicesRepository.insertInvoiceBuffer(invoice);
            for(VisitsParts position: visit.getParts()){
                CarPart part = position.getPart();
                InvoiceBufferPosition invoicePosition = new InvoiceBufferPosition(position, part.getName(), part.getTax(), invoice, "szt.");
                invoice.getInvoiceBufferPositions().add(invoicePosition);
                grossValue = grossValue.add(invoicePosition.getGrossPrice());
                netValue = netValue.add(invoicePosition.getNetPrice());
                invoicesRepository.insertInvoiceBufferPosition(invoicePosition);
            }
            for(VisitsHasServices position: visit.getServices()){
                Service service = position.getService();
                InvoiceBufferPosition invoicePosition = new InvoiceBufferPosition(position, service.getName(), service.getTax(), invoice, "h");
                invoice.getInvoiceBufferPositions().add(invoicePosition);
                grossValue = grossValue.add(invoicePosition.getGrossPrice());
                netValue = netValue.add(invoicePosition.getNetPrice());
                invoicesRepository.insertInvoiceBufferPosition(invoicePosition);
            }
            if (invoice != null) {
                invoice.setNetValue(netValue);
                invoice.setGrossValue(grossValue);
                invoicesRepository.updateInvoiceBuffer(invoice);
                return Response.status(200).entity(new AccessTokenForm(form.getAccessToken())).build();
            } else
                return Response.status(500).entity(new ErrorResponse("Nie udało się utworzyć faktury", form.getAccessToken())).build();

        }
        return Response.status(400).entity(new ErrorResponse("Brak potrzebnych danych", form.getAccessToken())).build();
    }

    @POST
    @Path("/getInvoicesList")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoicesList(AccessTokenForm form){
        Employee employee = (Employee)employeesRepository.findByToken(form.getAccessToken());
        if (employee == null)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        List<Invoice> invoices = invoicesRepository.getAllInvoices();
        InvoiceResponse[] invoicesArray = new InvoiceResponse[invoices.size()];
        int i = 0;
        for(Invoice invoice : invoices){
            invoicesArray[i] = new InvoiceResponse(invoice);
            i++;
        }
        return Response.status(200).entity(new AllInvoicesResponse(form.getAccessToken(), invoicesArray)).build();
    }

    @POST
    @Path("/getProFormaInvoicesList")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProFormaInvoicesList(AccessTokenForm form){
        Employee employee = (Employee)employeesRepository.findByToken(form.getAccessToken());
        if (employee == null)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        List<InvoiceBuffer> invoices = invoicesRepository.getAllProFormaInvoices();
        InvoiceResponse[] invoicesArray = new InvoiceResponse[invoices.size()];
        int i = 0;
        for(InvoiceBuffer invoice : invoices){
            invoicesArray[i] = new InvoiceResponse(invoice);
            i++;
        }
        return Response.status(200).entity(new AllInvoicesResponse(form.getAccessToken(), invoicesArray)).build();
    }
}
