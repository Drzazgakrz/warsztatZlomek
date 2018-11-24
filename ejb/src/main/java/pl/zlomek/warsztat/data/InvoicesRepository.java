package pl.zlomek.warsztat.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.zlomek.warsztat.model.*;

import javax.ejb.NoSuchEntityException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.List;

@ApplicationScoped
public class InvoicesRepository {

    @Inject
    private EntityManager em;

    @Transactional
    public void insertInvoice(Invoice invoice) {
        em.persist(invoice);
    }

    public void insertInvoicePosition(InvoicePosition position) {
        em.persist(position);
    }

    private Logger log = LoggerFactory.getLogger(InvoicesRepository.class);

    public Long countInvoicesInMonth() {
        try {
            log.info(em.toString());
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(invoice.id) FROM Invoice invoice WHERE " +
                    "Invoice.invoiceNumber LIKE :invoiceNumber", Long.class);
            StringBuilder regexBuilder = new StringBuilder("%/").append(LocalDate.now().getMonthValue());
            String regex = regexBuilder.append("/").append(LocalDate.now().getYear()).toString();
            log.info(regex);
            query.setParameter("invoiceNumber", regex);
            return query.getSingleResult() + 1;
        } catch (NoSuchEntityException e) {
            return 1L;
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public Invoice getInvoiceById(long id) {
        try {
            TypedQuery<Invoice> query = em.createQuery("SELECT invoice FROM Invoice invoice WHERE invoice.id = :id", Invoice.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public InvoiceBuffer getInvoiceBufferById(long id) {
        try {
            TypedQuery<InvoiceBuffer> query = em.createQuery("SELECT invoice FROM InvoiceBuffer invoice WHERE invoice.id = :id",
                    InvoiceBuffer.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void updateInvoice(Invoice invoice) {
        em.merge(invoice);
    }

    public void insertInvoiceBufferPosition(InvoiceBufferPosition position) {
        em.persist(position);
    }

    public void insertInvoiceBuffer(InvoiceBuffer position) {
        em.persist(position);
    }

    public void updateInvoiceBuffer(InvoiceBuffer invoice) {
        em.merge(invoice);
    }

    public List<Invoice> getAllInvoices() {
        try {
            TypedQuery<Invoice> query = em.createQuery("SELECT invoice FROM Invoice invoice WHERE invoice.corectionInvoice is null ORDER BY invoice.dayOfIssue DESC ", Invoice.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<InvoiceBuffer> getAllProFormaInvoices() {
        try {
            TypedQuery<InvoiceBuffer> query = em.createQuery("SELECT invoice FROM InvoiceBuffer invoice WHERE invoice.invoiceBufferStatus = :status ORDER BY invoice.dayOfIssue DESC", InvoiceBuffer.class);
            query.setParameter("status", InvoiceBufferStatus.proForma);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
