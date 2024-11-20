package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends CrudRepository<Contact, Long> {
    Optional<Contact> findByPhone(String phone);

    @Query(nativeQuery = true, value = """
            SELECT
              deal_id
            from
              contact
              JOIN (
                SELECT
                  deal_id,
                  deal.contact_id
                FROM
                  event
                  JOIN (
                    SELECT
                      id as deal_id,
                      deal.contact_id,
                      deal.event_id
                    from
                      deal
                    WHERE
                      paid IS TRUE and type = 'СБОРНЫЕ ГРУППЫ'
                  ) as deal on event.id = deal.event_id
                  WHERE event.name :: Timestamp :: date = now() :: date + interval '3 days'
              ) as time_event on contact.id = time_event.contact_id""")
    List<Long> findAllContactsDealWhenStartEventAfterThreeDays();

}
