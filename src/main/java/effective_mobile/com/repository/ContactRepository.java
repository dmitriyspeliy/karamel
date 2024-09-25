package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Contact;
import org.springframework.data.repository.CrudRepository;

public interface ContactRepository extends CrudRepository<Contact, Long> {
}
