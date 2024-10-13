package effective_mobile.com.service;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.service.api.event.FetchAllSlot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class CashedEvent {
    private final String cleanRate = "300000"; //5 mins
    private final FetchAllSlot fetchAllSlot;

    @CacheEvict(value = "json-nodes", allEntries = true)
    @Scheduled(fixedRateString = cleanRate)
    @Async("jobExecutor")
    public void emptyCache() {
        log.info("Clean cashed json-nodes");
    }

    @Cacheable(value = "json-nodes", key = "#cityName + #type")
    @Async("jobExecutor")
    public ArrayList<JsonNode> cashedEvent(String cityName, String type) {
        log.info("Cashed with parameters: {}, {}", cityName, type);
        return fetchAllSlot.fetchAllSlotByCityAndType(cityName, type);
    }


}
