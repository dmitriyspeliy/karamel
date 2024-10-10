package effective_mobile.com.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "robokassa")
@Getter
@Setter
public class RobokassaProperties {
    private String idUrl;
    private String invoiceUrl;
    private String invoiceStatus;
}
