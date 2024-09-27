package effective_mobile.com.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.payment-service")
@Getter
@Setter
public class PaymentProperties {
    private String password, password2;
    private String merchantName;
    private String acquiringServiceHost;
    private String acquiringServicePaymentBaseUrl;
    private String apiVersion;
    private String sno, inn, tax, location, paymentMethod, paymentObject;
}