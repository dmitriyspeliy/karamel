package effective_mobile.com.model.dto.rs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.time.Instant;

@Builder
@Getter
@Setter
public class GetBookingCartResponse {
    private Long leadId;
    private Long contactId;
    private String name;
    private String managerContactNumber;
    private URL paymentLink;
    private Integer kidCount;
    private Integer adultCount;
    private Integer kidPrice;
    private Integer adultPrice;
    private Integer totalPrice;
    private String kidAge;
    private String address;
    private Instant time;
    private Instant createdAt;
}