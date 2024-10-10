package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Invoice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    Optional<Invoice> findByExtInvoiceId(String hash);

    @Query(nativeQuery = true, value = "select * from invoice where status = ?1")
    List<Invoice> getInvoiceByStatus(String status);

    @Query(nativeQuery = true, value = "select * from invoice where status = ?1 and count_of_send_ticket = 0")
    List<Invoice> getInvoiceByStatusAndCountOfSendTicket(String status);

}
