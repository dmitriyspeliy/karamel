package effective_mobile.com.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;

@Builder
@Getter
@Setter
public class Cart {
    private Long leadId;
    private Long contactId;

    private String name;
    private String managerContactNumber;

    private URL paymentLink;

    private Integer kidCount;
    private Integer adultCount;
    private BigDecimal kidPrice;
    private BigDecimal adultPrice;
    private BigDecimal totalPrice;

    private String kidAge;
    private String address;
    private Instant time;
    private Instant createdAt;

}