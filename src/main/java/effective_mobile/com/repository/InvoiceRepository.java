package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.utils.enums.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    Optional<Invoice> findByExtInvoiceId(String hash);

    @EntityGraph(value = "invoice_deal_event")
    @Query("select inv from Invoice inv where inv.extInvoiceId = ?1")
    Optional<Invoice> getDealAndEvent(String hash);

    @EntityGraph(value = "invoice_deal_event")
    @Query("select inv from Invoice inv where inv.status = ?1 and inv.createAt < ?2")
    List<Invoice> getDealAndEventAndContactByStatusAndCreateTimeLessThan(Status status, LocalDateTime localDateTime);

    @EntityGraph(value = "invoice_deal_event")
    @Query("select inv from Invoice inv where inv.status = ?1 and inv.createAt > ?2")
    List<Invoice> getDealAndEventAndContactByStatusAndCreateTimeMoreThan(Status status, LocalDateTime localDateTime);

    @EntityGraph(value = "invoice_deal_event")
    @Query("select inv from Invoice inv where inv.status = ?1 and inv.countOfSendTicket = 0")
    List<Invoice> getDealAndEventAndContactByStatus(Status status);

    @EntityGraph(value = "invoice_deal_event")
    @Query("select inv from Invoice inv where inv.status = ?1 and inv.countOfSendSms = 0")
    List<Invoice> getDealAndEventAndContactByStatusSms(Status status);

}
