package effective_mobile.com.model.dto.rs;

import java.io.Serializable;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

public record GetPaymentLinkResponse(String invoiceId, URL url, Instant created) implements Serializable {
}