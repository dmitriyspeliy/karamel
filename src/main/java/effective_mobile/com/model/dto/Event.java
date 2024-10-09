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
    private Long adultPrice;
    private Long kidPrice;
    private String childAge;
    private Long capacity;
    private Long adultCapacity;
    private Long kidCapacity;
    private String gatheringType;
    private Boolean adultRequired;
}