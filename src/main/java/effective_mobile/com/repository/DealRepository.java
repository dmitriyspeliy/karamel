package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Deal;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DealRepository extends CrudRepository<Deal, Long> {
    Optional<Deal> findByExtDealId(Long extId);
}
