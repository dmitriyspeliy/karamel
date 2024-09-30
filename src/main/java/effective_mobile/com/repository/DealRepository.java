package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Deal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DealRepository extends CrudRepository<Deal, Long> {
    Optional<Deal> findByExtDealId(String extId);

    @Query(nativeQuery = true, value =
            "select ext_deal_id from deal inner join contact on deal.contact_id = contact.id where ext_contact_id = ?1 and event_id = ?2 order by create_date desc limit 1")
    String findDealByContactIdAdnEventId(String contactId, Long eventId);


}
