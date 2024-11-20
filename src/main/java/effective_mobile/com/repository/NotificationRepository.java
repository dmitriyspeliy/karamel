package effective_mobile.com.repository;

import effective_mobile.com.model.entity.Notification;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
}
