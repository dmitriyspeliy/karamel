package effective_mobile.com.model.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event implements Serializable {
    private Long id;
    private String name;
    private String type;
    private String time;
    private Integer adultPrice;
    private Integer kidPrice;
    private Integer childAge;
    private Integer capacity;
    private Integer adultCapacity;
    private Integer kidCapacity;
    private Integer slotsLeft;
    private Integer adultSlotsLeft;
    private Integer kidSlotsLeft;
    private String gatheringType;
    private Boolean adultRequired;
    private String city;
}