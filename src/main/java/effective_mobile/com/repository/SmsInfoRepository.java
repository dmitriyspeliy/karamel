package effective_mobile.com.repository;

import effective_mobile.com.model.entity.SmsInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsInfoRepository extends CrudRepository<SmsInfo, Long> {
    Optional<SmsInfo> findByExtSmsId(String extSmsId);
}