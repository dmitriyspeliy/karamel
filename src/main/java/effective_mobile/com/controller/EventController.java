package effective_mobile.com.controller;

import effective_mobile.com.model.dto.Event;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/events")
public class EventController {

    @GetMapping("/group")
    public Event getUpcomingGroupEvents() {

        Event event = Event.builder()
                .id(1L)
                .name("name")
                .type("type")
                .city("city")
                .time(Instant.now())
                .adultPrice(BigDecimal.ONE)
                .kidPrice(BigDecimal.ONE)
                .adultRequired(true)
                .curators("curators")
                .meetingAddress("adress")
                .ageRestriction("10")
                .childAge("10")
                .capacity(10L)
                .adultCapacity(10L)
                .kidCapacity(10L)
                .slotsLeft(10L)
                .adultSlotsLeft(10L)
                .kidSlotsLeft(10L)
                .gatheringType("type")
                .build();

        return event;
    }

    @GetMapping("/mixed")
    public Event getUpcomingMixedEvents() {
        Event event = Event.builder()
                .id(1L)
                .name("name")
                .type("type")
                .city("city")
                .time(Instant.now())
                .adultPrice(BigDecimal.ONE)
                .kidPrice(BigDecimal.ONE)
                .adultRequired(true)
                .curators("curators")
                .meetingAddress("adress")
                .ageRestriction("10")
                .childAge("10")
                .capacity(10L)
                .adultCapacity(10L)
                .kidCapacity(10L)
                .slotsLeft(10L)
                .adultSlotsLeft(10L)
                .kidSlotsLeft(10L)
                .gatheringType("type")
                .build();

        return event;
    }

}
