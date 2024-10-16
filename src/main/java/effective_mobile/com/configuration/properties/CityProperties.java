package effective_mobile.com.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "spring")
@Getter
@Setter
public class CityProperties {

    private Map<String, Info> cityInfo = new HashMap<>();

    @Getter
    @Setter
    public static class Info {

        private String bitrixFieldNum;
        private String cityName;
        private Instant startEndpoint;
        private Instant finishEndpoint;
        private String address;
        private List<String> managerContactNumbers;
        private String vkLink;
        private String offerLink;

        private PaymentInfo paymentInfo;

        @Getter
        @Setter
        public static class PaymentInfo {
            private String robokassaPass1;
            private String robokassaPass2;
            private String robokassaPass1Test;
            private String robokassaPass2Test;
            private String login;
            private String sno;
            private String tax;
        }
    }
}
