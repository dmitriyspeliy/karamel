package effective_mobile.com.service;

import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CashedUserInfo {

    private final DealRepository dealRepository;

    @CacheEvict(value = "user-info", allEntries = true)
    @Scheduled(cron = "0 0 3 * * *")
    public void emptyCache() {
        log.info("Clean cashed user-info");
    }

    @Cacheable(value = "user-info", key = "#dealId")
    public Deal cashedUserInfo(Long dealId) {
        log.info("User info was cashed with deal id: {}", dealId);
        return dealRepository.findByIdDealWithEventContact(dealId).orElse(null);
    }

}
