package effective_mobile.com.model.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event implements Serializable {
    private Long id;
    private String name;
    private String type;
    private Instant time;
    private BigDecimal adultPrice;
    private BigDecimal kidPrice;
    private Boolean adultRequired;
    private String curators;
    private String meetingAddress;
    private String ageRestriction;
    private String childAge;
    private Long capacity;
    private Long adultCapacity;
    private Long kidCapacity;
    private Long slotsLeft;
    private Long adultSlotsLeft;
    private Long kidSlotsLeft;
    private String gatheringType;
    private String city;
}