package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Contact;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ContactRepository extends CrudRepository<Contact, Long> {
    Optional<Contact> findByPhone(String phone);
}
