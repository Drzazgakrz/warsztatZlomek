package pl.zlomek.warsztat.data;

import pl.zlomek.warsztat.model.Invoice;

import javax.ejb.NoSuchEntityException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class InvoicesRepository {

    @Inject
    EntityManager em;


    public int countInvoicesInMonth(){
        try {
            StringBuilder regexBuilder = new StringBuilder("%/").append(GregorianCalendar.MONTH);
            String regex = regexBuilder.append("/").append(GregorianCalendar.YEAR).toString();
            TypedQuery<Integer> query = em.createQuery("SELECT COUNT(invoice) FROM Invoice invoice WHERE Invoice.invoiceNumber LIKE :invoiceNumber", Integer.class);
            query.setParameter("invoiceNumber", regex);
            return query.getSingleResult()+1;
        }catch (NoSuchEntityException e){
            return 1;
        }
        catch (Exception e){
            return -1;
        }


    }
}
