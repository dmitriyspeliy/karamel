package ru.kotomore.excursionapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Lead {
    private String eventType;
    private String address;
    private Integer prepayment;
    private Integer ticketPrice;
    private Integer childCount;
    private Integer adultsCount;
    private Integer childFreeCount;
    private Integer adultsFreeCount;
    private Integer peopleCount;
    private Integer childAge;
    private Long date;
}
