package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Invoice;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    Optional<Invoice> findByExtInvoiceId(String hash);
}
