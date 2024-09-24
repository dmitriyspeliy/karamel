package effective_mobile.com.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "spring")
@Getter
@Setter
public class CityProperties {
    Map<String, Info> cityInfo = new HashMap<>();

    @Getter
    @Setter
    public static class Info {
        Integer timezone;
        Long invoiceLifetimeMinutes;
        String cityName;
        Instant startEndpoint;
        Instant finishEndpoint;
        String address;
        List<String> managerContactNumbers;
        String vkLink;
        String offerLink;
        BigDecimal reservationPaymentAmount;
        Integer maxCapacity;
        Integer minCapacity;
        Integer refundPeriod;

        public ZoneOffset getTimezone() {
            return ZoneOffset.ofHours(timezone);
        }
    }
}
