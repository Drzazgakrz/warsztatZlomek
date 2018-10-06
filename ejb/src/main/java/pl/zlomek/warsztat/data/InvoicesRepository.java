package pl.zlomek.warsztat.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.model.Invoice;

import javax.ejb.NoSuchEntityException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.GregorianCalendar;

@ApplicationScoped
public class InvoicesRepository {

    @Inject
    private EntityManager em;

    @Transactional
    public void insertInvoice(Invoice invoice){
        em.persist(invoice);
    }

    private Logger log = LoggerFactory.getLogger(InvoicesRepository.class);

    public Long countInvoicesInMonth(){
        try {
            log.info(em.toString());
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(invoice.id) FROM Invoice invoice WHERE " +
                    "Invoice.invoiceNumber LIKE :invoiceNumber", Long.class);
            StringBuilder regexBuilder = new StringBuilder("%/").append(GregorianCalendar.MONTH);
            String regex = regexBuilder.append("/").append(GregorianCalendar.YEAR).toString();
            query.setParameter("invoiceNumber", regex);
            return query.getSingleResult()+1;
        }catch (NoSuchEntityException e){
            return 1L;
        }
        catch (Exception e){
            e.printStackTrace();
            return -1L;
        }
    }
}
