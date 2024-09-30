package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Deal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DealRepository extends CrudRepository<Deal, Long> {
    Optional<Deal> findByExtDealId(String extId);

    @Query(nativeQuery = true, value =
            "select * from deal inner join contact on deal.contact_id = contact.id where ext_contact_id = :contactId and event_id = :eventId order by create_date desc limit 1")
    Optional<Deal> findDealByContactIdAdnEventId(@Param(value = "contactId") String contactId,
                                                 @Param(value = "eventId") Long eventId);


}
