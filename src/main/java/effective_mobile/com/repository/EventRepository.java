package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EventRepository extends CrudRepository<Event, Long> {
    Optional<Event> findByExtEventId(Long extId);
    Optional<Event> findByName(String name);
}
