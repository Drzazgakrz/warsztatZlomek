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
import java.util.stream.Collectors;

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
        Visit visit = visitsRepository.getVisitById(newInvoice.getVisitId());
        if(visit == null || visit.getVisitFinished() == null){
            return Response.status(400).entity(new ErrorResponse("Brak takiej wizyty lub jest ona nie ukończona",
                    newInvoice.getAccessToken())).build();
        }
        CompanyData companyData = getCompanyData(company);
        Invoice invoice = createInvoice(newInvoice, companyData, visit);
        if (invoice != null) {
            invoicesRepository.updateInvoice(invoice);
            return Response.status(200).entity(new InvoiceDetailsResponse(newInvoice.getAccessToken(), invoice)).build();
        } else
            return Response.status(500).entity(new ErrorResponse("Nie udało się utworzyć faktury", newInvoice.getAccessToken())).build();
    }

    public CompanyData getCompanyData(Company company){
        List<CompanyData> companies = companyDataRespository.
                getAllCompanies(company.getCompanyName()).
                stream().filter((companyData ->
                companyData.compareCompanies(company))).limit(1).
                collect(Collectors.toList());
        CompanyData companyData;
        if(companies.size() == 0){
            companyData = new CompanyData(company);
            companyDataRespository.insert(companyData);
            return companyData;
        }
        else
            return companies.get(0);
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

    public Invoice createInvoice(AddInvoiceForm newInvoice, CompanyData companyData, Visit visit) {
        try {
            int discount = newInvoice.getDiscount();

            MethodOfPayment methodOfPayment = createMethodOfPayment(newInvoice.getMethodOfPayment());

            CarServiceData carServiceData = carServiceDataRespository.getTopServiceData();
            if (carServiceData != null && companyData != null && methodOfPayment != null) {
                LocalDate paymentDate = newInvoice.getPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Invoice invoice = new Invoice(discount, methodOfPayment, companyData, carServiceData, paymentDate, generateInvoiceNumber());
                invoicesRepository.insertInvoice(invoice);

                LocalDate finishDate = (LocalDate.now().equals(visit.getVisitDate().toLocalDate()))?
                        null : visit.getVisitDate().toLocalDate();
                invoice.setVisitFinished(finishDate);
                TaxModel tax = addPositions(visit, invoice);
                invoice.setNetValue(tax.getNetValue());
                invoice.setGrossValue(tax.getGrossValue());
                return invoice;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public TaxModel addPositions(Visit visit, Invoice invoice){
        BigDecimal grossValue = new BigDecimal(0);
        BigDecimal netValue = new BigDecimal(0);
        for(VisitsParts position: visit.getParts()){
            CarPart part = position.getPart();
            InvoicePosition invoicePosition = new InvoicePosition(position, part.getProducer()+ " " +
                    part.getName(), part.getTax(), invoice, "szt.");
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
        return new TaxModel(netValue, grossValue);
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
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if(visit == null){
            return Response.status(400).entity(new ErrorResponse("Brak takiej wizyty", form.getAccessToken())).build();
        }
        Invoice newInvoice = createInvoice(form, invoice.getCompanyData(), visit);
        if (newInvoice == null) {
            return Response.status(400).entity(new ErrorResponse("Nie utworzono nowej faktury", form.getAccessToken())).build();
        }
        invoicesRepository.insertInvoice(newInvoice);
        invoice.setCorectionInvoice(newInvoice);
        invoicesRepository.updateInvoice(invoice);
        return Response.status(200).entity(new InvoiceDetailsResponse(form.getAccessToken(),invoice)).build();
    }

    public CompanyDataBuffer getCompanyDataBuffer(Company company){
        List<CompanyDataBuffer> companies = companyDataRespository.
                getAllCompaniesBuffer(company.getCompanyName()).
                stream().filter((companyData ->
                companyData.compareCompanies(company))).limit(1).
                collect(Collectors.toList());
        CompanyDataBuffer companyData;
        if(companies.size() == 0){
            companyData = new CompanyDataBuffer(company);
            companiesRepository.insertComapnyDataBuffer(companyData);
            return companyData;
        }
        else
            return companies.get(0);
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
        if(company == null){
            return Response.status(400).entity(new ErrorResponse("brak firmy o podanej nazwie", form.getAccessToken())).build();
        }
        CompanyDataBuffer companyData = getCompanyDataBuffer(company);

        CarServiceData carServiceData = carServiceDataRespository.getTopServiceData();
        Visit visit = visitsRepository.getVisitById(form.getVisitId());
        if (carServiceData != null && companyData != null) {
            InvoiceBuffer invoice = createInvoiceBuffer(form, carServiceData, companyData, visit);
            if (invoice != null) {
                return Response.status(200).entity(new InvoiceDetailsResponse(form.getAccessToken(), invoice)).build();
            } else
                return Response.status(500).entity(new ErrorResponse("Nie udało się utworzyć faktury", form.getAccessToken())).build();

        }
        return Response.status(400).entity(new ErrorResponse("Brak potrzebnych danych", form.getAccessToken())).build();
    }

    public InvoiceBuffer createInvoiceBuffer(AddInvoiceForm form, CarServiceData carServiceData, CompanyDataBuffer companyData, Visit visit) {
        MethodOfPayment methodOfPayment = createMethodOfPayment(form.getMethodOfPayment());
        LocalDate paymentDate = form.getPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        InvoiceBuffer invoice = new InvoiceBuffer(form.getDiscount(), methodOfPayment, companyData, carServiceData, paymentDate, visit);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setInvoiceBufferStatus(InvoiceBufferStatus.proForma);
        invoicesRepository.insertInvoiceBuffer(invoice);
        companyData.addInvoice(invoice);
        companiesRepository.updateCompanyDataBuffer(companyData);
        TaxModel tax = saveInvoiceBufferPositions(visit, invoice);
        invoice.setNetValue(tax.getNetValue());
        invoice.setGrossValue(tax.getGrossValue());
        invoicesRepository.updateInvoiceBuffer(invoice);
        return invoice;
    }

    public TaxModel saveInvoiceBufferPositions(Visit visit, InvoiceBuffer invoice) {
        BigDecimal grossValue = new BigDecimal(0);
        BigDecimal netValue = new BigDecimal(0);
        for (VisitsParts position : visit.getParts()) {
            CarPart part = position.getPart();
            InvoiceBufferPosition invoicePosition = new InvoiceBufferPosition(position, part.getName(),
                    part.getTax(), invoice, "szt.");
            invoice.getInvoiceBufferPositions().add(invoicePosition);
            grossValue = grossValue.add(invoicePosition.getGrossPrice());
            netValue = netValue.add(invoicePosition.getNetPrice());
            invoicesRepository.insertInvoiceBufferPosition(invoicePosition);
        }
        for (VisitsHasServices position : visit.getServices()) {
            Service service = position.getService();
            InvoiceBufferPosition invoicePosition = new InvoiceBufferPosition(position,
                    service.getName(), service.getTax(), invoice, "h");
            invoice.getInvoiceBufferPositions().add(invoicePosition);
            grossValue = grossValue.add(invoicePosition.getGrossPrice());
            netValue = netValue.add(invoicePosition.getNetPrice());
            invoicesRepository.insertInvoiceBufferPosition(invoicePosition);
        }
        return new TaxModel(netValue, grossValue);
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

    @POST
    @Path("/getInvoiceDetails")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoiceDetails(GetInvoiceDetailsForm form){
        Employee employee = (Employee)employeesRepository.findByToken(form.getAccessToken());
        if (employee == null)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        Invoice invoice = invoicesRepository.getInvoiceById(form.getId());
        return Response.status(200).entity(new InvoiceDetailsResponse(form.getAccessToken(),invoice)).build();
    }

    private String generateInvoiceNumber(){
        StringBuilder invoiceNumberBuilder = new StringBuilder().append(invoicesRepository.countInvoicesInMonth());
        return invoiceNumberBuilder.append("/").append(LocalDate.now().getMonthValue()).append("/").append(LocalDate.now().getYear()).toString();

    }

    @POST
    @Path("/acceptProFormaInvoice")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptProFormaInvoice(AcceptProFormaInvoice form){
        Employee employee = (Employee)employeesRepository.findByToken(form.getAccessToken());
        if (employee == null)
            return Response.status(401).entity(new ErrorResponse("Autoryzacja nie powiodła się", null)).build();
        InvoiceBuffer proForma = invoicesRepository.getInvoiceBufferById(form.getProFormaInvoiceId());
        if(proForma == null)
            return Response.status(400).entity(new ErrorResponse("Brak faktury o takim id", form.getAccessToken())).build();
        if(proForma.getVisit() == null || proForma.getVisit().getVisitFinished() == null){
            return Response.status(400).entity(new ErrorResponse("Brak wizyty przypisanej do faktury", form.getAccessToken())).build();
        }
        List<CompanyData> companies = companyDataRespository.getAllCompanies(proForma.getCompanyDataBuffer().getCompanyName()).
                stream().filter((companyData -> companyData.compareCompanies(proForma.getCompanyDataBuffer()))).limit(1).collect(Collectors.toList());
        CompanyData companyData;
        if(companies.size() == 0){
            Company company = companiesRepository.getCompanyByName(proForma.getCompanyDataBuffer().getCompanyName());
            companyData = new CompanyData(company);
            companyDataRespository.insert(companyData);
        }
        else
            companyData = companies.get(0);
        Invoice invoice = new Invoice(proForma, companyData, carServiceDataRespository.getTopServiceData());
        invoicesRepository.insertInvoice(invoice);
        companyData.getInvoices().add(invoice);
        companyDataRespository.update(companyData);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        for(InvoiceBufferPosition position: proForma.getInvoiceBufferPositions()){
            InvoicePosition invoicePosition = new InvoicePosition(position, invoice);
            invoicesRepository.insertInvoicePosition(invoicePosition);
        }
        invoicesRepository.updateInvoice(invoice);
        return Response.status(200).entity(new InvoiceDetailsResponse(form.getAccessToken(), invoice)).build();
    }
}
